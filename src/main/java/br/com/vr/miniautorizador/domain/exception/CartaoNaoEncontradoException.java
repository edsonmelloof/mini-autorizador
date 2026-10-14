package br.com.vr.miniautorizador.domain.exception;

public class CartaoNaoEncontradoException extends RuntimeException {

	public CartaoNaoEncontradoException(String numeroCartao) {
		super("Cartão não encontrado: " + numeroCartao);
	}
}
