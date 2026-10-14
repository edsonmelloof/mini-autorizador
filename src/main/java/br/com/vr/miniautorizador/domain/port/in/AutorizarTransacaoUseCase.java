package br.com.vr.miniautorizador.domain.port.in;

import br.com.vr.miniautorizador.domain.model.ResultadoAutorizacao;
import br.com.vr.miniautorizador.domain.model.Transacao;

public interface AutorizarTransacaoUseCase {

	ResultadoAutorizacao autorizar(Transacao transacao);
}
