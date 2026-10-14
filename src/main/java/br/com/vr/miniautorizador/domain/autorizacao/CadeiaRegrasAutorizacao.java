package br.com.vr.miniautorizador.domain.autorizacao;

import br.com.vr.miniautorizador.domain.model.ResultadoAutorizacao;

import java.util.List;

public final class CadeiaRegrasAutorizacao {

	private final List<RegraAutorizacao> regras;

	public CadeiaRegrasAutorizacao(List<RegraAutorizacao> regras) {
		this.regras = List.copyOf(regras);
	}

	public ResultadoAutorizacao avaliar(ContextoAutorizacao contexto) {
		return regras.stream()
			.map(regra -> regra.avaliar(contexto))
			.flatMap(java.util.Optional::stream)
			.findFirst()
			.map(ResultadoAutorizacao::negada)
			.orElseGet(ResultadoAutorizacao::aprovada);
	}
}
