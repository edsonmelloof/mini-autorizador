package br.com.vr.miniautorizador.domain.exception;

public class CartaoJaExistenteException extends RuntimeException {

	private final String numeroCartao;
	private final String senha;

	public CartaoJaExistenteException(String numeroCartao, String senha) {
		super("Cartão já existe");
		this.numeroCartao = numeroCartao;
		this.senha = senha;
	}

	public String numeroCartao() {
		return numeroCartao;
	}

	public String senha() {
		return senha;
	}
}
