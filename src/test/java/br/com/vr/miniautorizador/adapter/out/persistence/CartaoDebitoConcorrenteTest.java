package br.com.vr.miniautorizador.adapter.out.persistence;

import br.com.vr.miniautorizador.domain.model.Cartao;
import br.com.vr.miniautorizador.domain.model.Dinheiro;
import br.com.vr.miniautorizador.domain.model.NumeroCartao;
import br.com.vr.miniautorizador.domain.port.out.CartaoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class CartaoDebitoConcorrenteTest {

	@Autowired
	private CartaoRepository cartaoRepository;

	@Test
	void doisDebitosDoSaldoTotalAprovamApenasUm() throws InterruptedException {
		NumeroCartao numero = NumeroCartao.de(UUID.randomUUID().toString().substring(0, 16));
		cartaoRepository.salvar(Cartao.reconstituir(numero, "hash", Dinheiro.de("10.00")));

		var start = new CountDownLatch(1);
		var done = new CountDownLatch(2);
		var aprovados = new AtomicInteger();

		try (var executor = Executors.newFixedThreadPool(2)) {
			Runnable tarefa = () -> {
				try {
					start.await();
					if (cartaoRepository.debitarSeHouverSaldo(numero, Dinheiro.de("10.00")) == 1) {
						aprovados.incrementAndGet();
					}
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

		assertThat(aprovados.get()).isEqualTo(1);
		assertThat(cartaoRepository.buscarPorNumero(numero))
			.get()
			.extracting(Cartao::saldo)
			.isEqualTo(Dinheiro.ZERO);
	}
}
