package br.com.vr.miniautorizador.adapter.in.web.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"senha", "numeroCartao"})
public record CartaoResponse(String senha, String numeroCartao) {
}
