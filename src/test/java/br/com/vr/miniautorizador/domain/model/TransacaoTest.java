package br.com.vr.miniautorizador.domain.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TransacaoTest {

	@Test
	void recusaSenhaEmBranco() {
		assertThatThrownBy(() -> new Transacao(NumeroCartao.de("1"), "  ", Dinheiro.de("1.00")))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessageContaining("Senha");
	}

	@Test
	void aceitaSenhaEValor() {
		var transacao = new Transacao(NumeroCartao.de("1"), "1234", Dinheiro.de("10.00"));
		assertThat(transacao.senha()).isEqualTo("1234");
		assertThat(transacao.valor()).isEqualTo(Dinheiro.de("10.00"));
	}
}
