package br.com.vr.miniautorizador.domain.port.out;

public interface CodificadorSenha {

	String codificar(String senhaPlana);

	boolean confere(String senhaPlana, String senhaHash);
}
