package br.com.vr.miniautorizador.domain.exception;

import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
public class CartaoJaExistenteException extends RuntimeException {

	private final String numeroCartao;
	private final String senha;

	public CartaoJaExistenteException(String numeroCartao, String senha) {
		super("Cartão já existe");
		this.numeroCartao = numeroCartao;
		this.senha = senha;
	}
}
