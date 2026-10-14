package br.com.vr.miniautorizador.domain.model;

import java.util.Objects;
import java.util.Optional;

public final class NumeroCartao {

	private final String valor;

	private NumeroCartao(String valor) {
		this.valor = valor;
	}

	public static NumeroCartao de(String valor) {
		return Optional.ofNullable(valor)
			.map(String::trim)
			.filter(v -> !v.isBlank())
			.map(NumeroCartao::new)
			.orElseThrow(() -> new IllegalArgumentException("Número do cartão é obrigatório"));
	}

	public String valor() {
		return valor;
	}

	@Override
	public boolean equals(Object o) {
		return Optional.ofNullable(o)
			.filter(NumeroCartao.class::isInstance)
			.map(NumeroCartao.class::cast)
			.filter(outro -> valor.equals(outro.valor))
			.isPresent();
	}

	@Override
	public int hashCode() {
		return Objects.hash(valor);
	}

	@Override
	public String toString() {
		return valor;
	}
}
