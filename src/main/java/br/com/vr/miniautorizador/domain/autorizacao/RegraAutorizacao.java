package br.com.vr.miniautorizador.domain.autorizacao;

import br.com.vr.miniautorizador.domain.model.MotivoNegacao;

import java.util.Optional;

public interface RegraAutorizacao {

	Optional<MotivoNegacao> avaliar(ContextoAutorizacao contexto);
}
