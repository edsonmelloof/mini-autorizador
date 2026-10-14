package br.com.vr.miniautorizador.domain.model;

import br.com.vr.miniautorizador.domain.port.out.CodificadorSenha;

import java.util.Objects;

public record Cartao(NumeroCartao numero, String senhaHash, Dinheiro saldo) {

	public Cartao {
		Objects.requireNonNull(numero);
		Objects.requireNonNull(senhaHash);
		Objects.requireNonNull(saldo);
	}

	public static Cartao novo(NumeroCartao numero, String senhaPlana, CodificadorSenha codec) {
		return new Cartao(numero, codec.codificar(senhaPlana), Dinheiro.SALDO_INICIAL);
	}

	public static Cartao reconstituir(NumeroCartao numero, String senhaHash, Dinheiro saldo) {
		return new Cartao(numero, senhaHash, saldo);
	}

	public boolean senhaConfere(String senhaInformada, CodificadorSenha codec) {
		return codec.confere(senhaInformada, senhaHash);
	}

	public boolean possuiSaldo(Dinheiro valor) {
		return saldo.cobre(valor);
	}
}
