package br.com.vr.miniautorizador.domain.model;

import java.util.Objects;
import java.util.Optional;

public record Transacao(NumeroCartao numeroCartao, String senha, Dinheiro valor) {

	public Transacao {
		Objects.requireNonNull(numeroCartao, "Número do cartão é obrigatório");
		Objects.requireNonNull(valor, "Valor da transação é obrigatório");
		senha = Optional.ofNullable(senha)
			.filter(s -> !s.isBlank())
			.orElseThrow(() -> new IllegalArgumentException("Senha da transação é obrigatória"));
	}
}
