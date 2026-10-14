package br.com.vr.miniautorizador.domain.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;
import java.util.Optional;

public final class Dinheiro {

	public static final Dinheiro SALDO_INICIAL = de("500.00");
	public static final Dinheiro ZERO = de("0.00");

	private final BigDecimal valor;

	private Dinheiro(BigDecimal valor) {
		this.valor = valor.setScale(2, RoundingMode.HALF_EVEN);
	}

	public static Dinheiro de(String valor) {
		return de(new BigDecimal(valor));
	}

	public static Dinheiro de(BigDecimal valor) {
		return Optional.ofNullable(valor)
			.filter(v -> v.signum() >= 0)
			.map(Dinheiro::new)
			.orElseThrow(() -> new IllegalArgumentException("Valor monetário deve ser positivo ou zero"));
	}

	public BigDecimal valor() {
		return valor;
	}

	public boolean cobre(Dinheiro outro) {
		return this.valor.compareTo(outro.valor) >= 0;
	}

	public Dinheiro subtrair(Dinheiro outro) {
		return Optional.of(this)
			.filter(atual -> atual.cobre(outro))
			.map(atual -> new Dinheiro(atual.valor.subtract(outro.valor)))
			.orElseThrow(() -> new IllegalArgumentException("Saldo insuficiente para subtrair"));
	}

	@Override
	public boolean equals(Object o) {
		return Optional.ofNullable(o)
			.filter(Dinheiro.class::isInstance)
			.map(Dinheiro.class::cast)
			.filter(outro -> valor.compareTo(outro.valor) == 0)
			.isPresent();
	}

	@Override
	public int hashCode() {
		return Objects.hash(valor);
	}

	@Override
	public String toString() {
		return valor.toPlainString();
	}
}
