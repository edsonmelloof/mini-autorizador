package br.com.vr.miniautorizador.application;

import br.com.vr.miniautorizador.domain.autorizacao.CadeiaRegrasAutorizacao;
import br.com.vr.miniautorizador.domain.autorizacao.CartaoExistenteRegra;
import br.com.vr.miniautorizador.domain.autorizacao.SaldoDisponivelRegra;
import br.com.vr.miniautorizador.domain.autorizacao.SenhaCorretaRegra;
import br.com.vr.miniautorizador.domain.model.Cartao;
import br.com.vr.miniautorizador.domain.model.Dinheiro;
import br.com.vr.miniautorizador.domain.model.MotivoNegacao;
import br.com.vr.miniautorizador.domain.model.NumeroCartao;
import br.com.vr.miniautorizador.domain.model.ResultadoAutorizacao;
import br.com.vr.miniautorizador.domain.model.Transacao;
import br.com.vr.miniautorizador.support.FakeCartaoRepository;
import br.com.vr.miniautorizador.support.PlainCodificadorSenha;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

class AutorizarTransacaoServiceTest {

	private static final NumeroCartao NUMERO = NumeroCartao.de("6549873025634501");

	private FakeCartaoRepository repository;
	private AutorizarTransacaoService service;

	@BeforeEach
	void setUp() {
		repository = new FakeCartaoRepository();
		service = new AutorizarTransacaoService(
			repository,
			new PlainCodificadorSenha(),
			new CadeiaRegrasAutorizacao(List.of(
				new CartaoExistenteRegra(),
				new SenhaCorretaRegra(),
				new SaldoDisponivelRegra()
			))
		);
	}

	@Test
	void autorizaEDebitaSaldo() {
		repository.salvar(Cartao.reconstituir(NUMERO, "1234", Dinheiro.de("500.00")));

		var resultado = service.autorizar(new Transacao(NUMERO, "1234", Dinheiro.de("10.00")));

		assertThat(resultado).isInstanceOf(ResultadoAutorizacao.Aprovada.class);
		assertThat(repository.obrigatorio(NUMERO).saldo()).isEqualTo(Dinheiro.de("490.00"));
	}

	@Test
	void naoAlteraSaldoQuandoSenhaInvalida() {
		repository.salvar(Cartao.reconstituir(NUMERO, "1234", Dinheiro.de("500.00")));

		var resultado = service.autorizar(new Transacao(NUMERO, "0000", Dinheiro.de("10.00")));

		assertThat(resultado).isEqualTo(ResultadoAutorizacao.negada(MotivoNegacao.SENHA_INVALIDA));
		assertThat(repository.obrigatorio(NUMERO).saldo()).isEqualTo(Dinheiro.de("500.00"));
	}

	@Test
	void naoAlteraSaldoQuandoSaldoInsuficiente() {
		repository.salvar(Cartao.reconstituir(NUMERO, "1234", Dinheiro.de("5.00")));

		var resultado = service.autorizar(new Transacao(NUMERO, "1234", Dinheiro.de("10.00")));

		assertThat(resultado).isEqualTo(ResultadoAutorizacao.negada(MotivoNegacao.SALDO_INSUFICIENTE));
		assertThat(repository.obrigatorio(NUMERO).saldo()).isEqualTo(Dinheiro.de("5.00"));
	}

	@Test
	void recusaCartaoInexistente() {
		var resultado = service.autorizar(new Transacao(NUMERO, "1234", Dinheiro.de("10.00")));
		assertThat(resultado).isEqualTo(ResultadoAutorizacao.negada(MotivoNegacao.CARTAO_INEXISTENTE));
	}

	@Test
	void duasTransacoesSimultaneasDoSaldoTotalAprovamApenasUma() throws InterruptedException {
		repository.salvar(Cartao.reconstituir(NUMERO, "1234", Dinheiro.de("10.00")));
		var transacao = new Transacao(NUMERO, "1234", Dinheiro.de("10.00"));
		var start = new CountDownLatch(1);
		var done = new CountDownLatch(2);
		var aprovadas = new AtomicInteger();

		try (var executor = Executors.newFixedThreadPool(2)) {
			Runnable tarefa = () -> {
				try {
					start.await();
					var resultado = service.autorizar(transacao);
					OptionalAprovada.contar(resultado, aprovadas);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				} finally {
					done.countDown();
				}
			};
			executor.submit(tarefa);
			executor.submit(tarefa);
			start.countDown();
			done.await();
		}

		assertThat(aprovadas.get()).isEqualTo(1);
		assertThat(repository.obrigatorio(NUMERO).saldo()).isEqualTo(Dinheiro.ZERO);
	}

	private static final class OptionalAprovada {
		private static void contar(ResultadoAutorizacao resultado, AtomicInteger aprovadas) {
			java.util.Optional.of(resultado)
				.filter(ResultadoAutorizacao.Aprovada.class::isInstance)
				.ifPresent(ok -> aprovadas.incrementAndGet());
		}
	}
}
