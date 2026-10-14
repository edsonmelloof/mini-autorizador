package br.com.vr.miniautorizador.domain.model;

public sealed interface ResultadoAutorizacao {

	record Aprovada() implements ResultadoAutorizacao {
	}

	record Negada(MotivoNegacao motivo) implements ResultadoAutorizacao {
	}

	static ResultadoAutorizacao aprovada() {
		return new Aprovada();
	}

	static ResultadoAutorizacao negada(MotivoNegacao motivo) {
		return new Negada(motivo);
	}
}
