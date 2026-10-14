package br.com.vr.miniautorizador.domain.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

public record Dinheiro(BigDecimal valor) {

	public static final Dinheiro SALDO_INICIAL = de("500.00");
	public static final Dinheiro ZERO = de("0.00");

	public Dinheiro {
		valor = Optional.ofNullable(valor)
			.filter(v -> v.signum() >= 0)
			.map(v -> v.setScale(2, RoundingMode.HALF_EVEN))
			.orElseThrow(() -> new IllegalArgumentException("Valor monetário deve ser positivo ou zero"));
	}

	public static Dinheiro de(String valor) {
		return de(new BigDecimal(valor));
	}

	public static Dinheiro de(BigDecimal valor) {
		return new Dinheiro(valor);
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
	public String toString() {
		return valor.toPlainString();
	}
}
