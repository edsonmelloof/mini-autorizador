package br.com.vr.miniautorizador.adapter.in.web.dto;

import jakarta.validation.constraints.NotBlank;

public record CartaoRequest(
	@NotBlank String numeroCartao,
	@NotBlank String senha
) {
}
