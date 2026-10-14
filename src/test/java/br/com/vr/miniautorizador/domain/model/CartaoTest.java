package br.com.vr.miniautorizador.domain.model;

import br.com.vr.miniautorizador.support.PlainCodificadorSenha;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CartaoTest {

	private final PlainCodificadorSenha codec = new PlainCodificadorSenha();

	@Test
	void novoCartaoNasceComSaldoDeQuinhentosESenhaCodificada() {
		Cartao cartao = Cartao.novo(NumeroCartao.de("123"), "1234", codec);

		assertThat(cartao.saldo()).isEqualTo(Dinheiro.SALDO_INICIAL);
		assertThat(cartao.senhaConfere("1234", codec)).isTrue();
		assertThat(cartao.senhaConfere("x", codec)).isFalse();
	}

	@Test
	void possuiSaldoComparaContraOValorDaTransacao() {
		Cartao cartao = Cartao.reconstituir(NumeroCartao.de("123"), "h", Dinheiro.de("10.00"));
		assertThat(cartao.possuiSaldo(Dinheiro.de("10.00"))).isTrue();
		assertThat(cartao.possuiSaldo(Dinheiro.de("10.01"))).isFalse();
	}
}
