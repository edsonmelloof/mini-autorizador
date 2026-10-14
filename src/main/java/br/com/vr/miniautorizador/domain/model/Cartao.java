package br.com.vr.miniautorizador.domain.model;

import br.com.vr.miniautorizador.domain.port.out.CodificadorSenha;

import java.util.Objects;

public final class Cartao {

	private final NumeroCartao numero;
	private final String senhaHash;
	private final Dinheiro saldo;

	private Cartao(NumeroCartao numero, String senhaHash, Dinheiro saldo) {
		this.numero = Objects.requireNonNull(numero);
		this.senhaHash = Objects.requireNonNull(senhaHash);
		this.saldo = Objects.requireNonNull(saldo);
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

	public NumeroCartao numero() {
		return numero;
	}

	public String senhaHash() {
		return senhaHash;
	}

	public Dinheiro saldo() {
		return saldo;
	}
}
