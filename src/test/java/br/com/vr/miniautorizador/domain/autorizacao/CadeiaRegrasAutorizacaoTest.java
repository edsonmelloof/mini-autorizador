package br.com.vr.miniautorizador.domain.autorizacao;

import br.com.vr.miniautorizador.domain.model.Cartao;
import br.com.vr.miniautorizador.domain.model.Dinheiro;
import br.com.vr.miniautorizador.domain.model.MotivoNegacao;
import br.com.vr.miniautorizador.domain.model.NumeroCartao;
import br.com.vr.miniautorizador.domain.model.ResultadoAutorizacao;
import br.com.vr.miniautorizador.domain.model.Transacao;
import br.com.vr.miniautorizador.support.PlainCodificadorSenha;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class CadeiaRegrasAutorizacaoTest {

	private static final NumeroCartao NUMERO = NumeroCartao.de("6549873025634501");
	private static final PlainCodificadorSenha CODEC = new PlainCodificadorSenha();

	private CadeiaRegrasAutorizacao cadeia;

	@BeforeEach
	void setUp() {
		cadeia = new CadeiaRegrasAutorizacao(List.of(
			new CartaoExistenteRegra(),
			new SenhaCorretaRegra(),
			new SaldoDisponivelRegra()
		));
	}

	@Test
	void aprovaQuandoCartaoExisteSenhaConfereEHaSaldo() {
		var resultado = cadeia.avaliar(contexto(cartaoComSaldo("500.00"), "1234", "10.00"));
		assertThat(resultado).isEqualTo(ResultadoAutorizacao.aprovada());
	}

	@Test
	void recusaCartaoInexistenteAntesDeValidarSenhaOuSaldo() {
		var resultado = cadeia.avaliar(contexto(Optional.empty(), "errada", "999.00"));
		assertThat(resultado).isEqualTo(ResultadoAutorizacao.negada(MotivoNegacao.CARTAO_INEXISTENTE));
	}

	@Test
	void recusaSenhaInvalidaMesmoComSaldoSuficiente() {
		var resultado = cadeia.avaliar(contexto(cartaoComSaldo("500.00"), "9999", "10.00"));
		assertThat(resultado).isEqualTo(ResultadoAutorizacao.negada(MotivoNegacao.SENHA_INVALIDA));
	}

	@Test
	void recusaSaldoInsuficienteQuandoSenhaEstaCorreta() {
		var resultado = cadeia.avaliar(contexto(cartaoComSaldo("5.00"), "1234", "10.00"));
		assertThat(resultado).isEqualTo(ResultadoAutorizacao.negada(MotivoNegacao.SALDO_INSUFICIENTE));
	}

	@Test
	void aprovaDebitoDoSaldoExato() {
		var resultado = cadeia.avaliar(contexto(cartaoComSaldo("10.00"), "1234", "10.00"));
		assertThat(resultado).isInstanceOf(ResultadoAutorizacao.Aprovada.class);
	}

	private Optional<Cartao> cartaoComSaldo(String saldo) {
		return Optional.of(Cartao.reconstituir(NUMERO, "1234", Dinheiro.de(saldo)));
	}

	private ContextoAutorizacao contexto(Optional<Cartao> cartao, String senha, String valor) {
		return new ContextoAutorizacao(
			cartao,
			new Transacao(NUMERO, senha, Dinheiro.de(valor)),
			CODEC
		);
	}
}
