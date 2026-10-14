package br.com.vr.miniautorizador.adapter.out.persistence;

import br.com.vr.miniautorizador.domain.model.Cartao;
import br.com.vr.miniautorizador.domain.model.Dinheiro;
import br.com.vr.miniautorizador.domain.model.NumeroCartao;
import br.com.vr.miniautorizador.domain.port.out.CartaoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class CartaoPersistenceAdapterTest {

	@Autowired
	private CartaoRepository cartaoRepository;

	@Test
	void persisteConsultaEDebitaAtomicamente() {
		NumeroCartao numero = NumeroCartao.de(UUID.randomUUID().toString().substring(0, 16));
		cartaoRepository.salvar(Cartao.reconstituir(numero, "hash", Dinheiro.de("10.00")));

		assertThat(cartaoRepository.existe(numero)).isTrue();
		assertThat(cartaoRepository.buscarPorNumero(numero))
			.get()
			.extracting(Cartao::saldo)
			.isEqualTo(Dinheiro.de("10.00"));

		assertThat(cartaoRepository.debitarSeHouverSaldo(numero, Dinheiro.de("10.00"))).isEqualTo(1);
		assertThat(cartaoRepository.buscarPorNumero(numero))
			.get()
			.extracting(Cartao::saldo)
			.isEqualTo(Dinheiro.ZERO);
		assertThat(cartaoRepository.debitarSeHouverSaldo(numero, Dinheiro.de("0.01"))).isZero();
	}
}
