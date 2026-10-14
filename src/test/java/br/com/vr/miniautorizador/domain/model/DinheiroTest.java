package br.com.vr.miniautorizador.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DinheiroTest {

	@Test
	void saldoInicialEQuinhentosReais() {
		assertThat(Dinheiro.SALDO_INICIAL.valor()).isEqualByComparingTo("500.00");
	}

	@Test
	void recusaValorNegativo() {
		assertThatThrownBy(() -> Dinheiro.de(new BigDecimal("-0.01")))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessageContaining("positivo");
	}

	@Test
	void cobreQuandoSaldoEMaiorOuIgual() {
		Dinheiro dez = Dinheiro.de("10.00");
		assertThat(dez.cobre(Dinheiro.de("10.00"))).isTrue();
		assertThat(dez.cobre(Dinheiro.de("10.01"))).isFalse();
	}

	@Test
	void subtraiSomenteQuandoHaSaldo() {
		assertThat(Dinheiro.de("10.00").subtrair(Dinheiro.de("3.50")))
			.isEqualTo(Dinheiro.de("6.50"));
		assertThatThrownBy(() -> Dinheiro.de("1.00").subtrair(Dinheiro.de("1.01")))
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void igualdadeIgnoraEscala() {
		assertThat(Dinheiro.de("10.0")).isEqualTo(Dinheiro.de("10.00"));
		assertThat(Dinheiro.de("10.0")).hasSameHashCodeAs(Dinheiro.de("10.00"));
		assertThat(Dinheiro.de("10.00")).isNotEqualTo("10.00");
		assertThat(Dinheiro.de("10.00").toString()).isEqualTo("10.00");
	}

	@Test
	void recusaNulo() {
		assertThatThrownBy(() -> Dinheiro.de((java.math.BigDecimal) null))
			.isInstanceOf(IllegalArgumentException.class);
	}
}
