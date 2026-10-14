package br.com.vr.miniautorizador.domain.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class NumeroCartaoTest {

	@Test
	void rejeitaNumeroVazio() {
		assertThatThrownBy(() -> NumeroCartao.de("  "))
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void rejeitaNulo() {
		assertThatThrownBy(() -> NumeroCartao.de(null))
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void preservaValorInformado() {
		assertThat(NumeroCartao.de("6549873025634501").valor()).isEqualTo("6549873025634501");
	}

	@Test
	void igualdadeEHashCodePorValor() {
		var a = NumeroCartao.de("6549873025634501");
		var b = NumeroCartao.de("6549873025634501");
		assertThat(a).isEqualTo(b).hasSameHashCodeAs(b);
		assertThat(a).isNotEqualTo(NumeroCartao.de("0000"));
		assertThat(a).isNotEqualTo("6549873025634501");
		assertThat(a.toString()).isEqualTo("6549873025634501");
	}
}
