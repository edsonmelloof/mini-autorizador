package br.com.vr.miniautorizador.adapter.out.crypto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BcryptCodificadorSenhaTest {

	private final BcryptCodificadorSenha codec = new BcryptCodificadorSenha();

	@Test
	void hashNaoEIgualASenhaPlanaEConfereDepois() {
		String hash = codec.codificar("1234");

		assertThat(hash).isNotEqualTo("1234");
		assertThat(codec.confere("1234", hash)).isTrue();
		assertThat(codec.confere("0000", hash)).isFalse();
	}
}
