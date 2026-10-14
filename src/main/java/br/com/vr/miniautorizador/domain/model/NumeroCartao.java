package br.com.vr.miniautorizador.domain.model;

import java.util.Optional;

public record NumeroCartao(String valor) {

	public NumeroCartao {
		valor = Optional.ofNullable(valor)
			.map(String::trim)
			.filter(v -> !v.isBlank())
			.orElseThrow(() -> new IllegalArgumentException("Número do cartão é obrigatório"));
	}

	public static NumeroCartao de(String valor) {
		return new NumeroCartao(valor);
	}

	@Override
	public String toString() {
		return valor;
	}
}
