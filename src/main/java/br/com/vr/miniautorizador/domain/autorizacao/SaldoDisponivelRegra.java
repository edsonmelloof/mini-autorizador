package br.com.vr.miniautorizador.domain.autorizacao;

import br.com.vr.miniautorizador.domain.model.MotivoNegacao;

import java.util.Optional;

public final class SaldoDisponivelRegra implements RegraAutorizacao {

	@Override
	public Optional<MotivoNegacao> avaliar(ContextoAutorizacao contexto) {
		return contexto.cartao()
			.filter(cartao -> !cartao.possuiSaldo(contexto.transacao().valor()))
			.map(cartao -> MotivoNegacao.SALDO_INSUFICIENTE);
	}
}
