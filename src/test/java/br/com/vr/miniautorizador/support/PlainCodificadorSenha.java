package br.com.vr.miniautorizador.support;

import br.com.vr.miniautorizador.domain.port.out.CodificadorSenha;

public class PlainCodificadorSenha implements CodificadorSenha {

	@Override
	public String codificar(String senhaPlana) {
		return senhaPlana;
	}

	@Override
	public boolean confere(String senhaPlana, String senhaHash) {
		return senhaHash.equals(senhaPlana);
	}
}
