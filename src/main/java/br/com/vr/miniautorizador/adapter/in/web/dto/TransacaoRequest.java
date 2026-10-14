package br.com.vr.miniautorizador.adapter.in.web.dto;

import br.com.vr.miniautorizador.domain.model.Dinheiro;
import br.com.vr.miniautorizador.domain.model.NumeroCartao;
import br.com.vr.miniautorizador.domain.model.Transacao;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record TransacaoRequest(
	@NotBlank String numeroCartao,
	@NotBlank String senhaCartao,
	@NotNull @DecimalMin(value = "0.01", inclusive = true) BigDecimal valor
) {
	public Transacao paraDominio() {
		return new Transacao(NumeroCartao.de(numeroCartao), senhaCartao, Dinheiro.de(valor));
	}
}
