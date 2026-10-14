package br.com.vr.miniautorizador.domain.autorizacao;

import br.com.vr.miniautorizador.domain.model.MotivoNegacao;

import java.util.Optional;

public final class SenhaCorretaRegra implements RegraAutorizacao {

	@Override
	public Optional<MotivoNegacao> avaliar(ContextoAutorizacao contexto) {
		return contexto.cartao()
			.filter(cartao -> !cartao.senhaConfere(contexto.transacao().senha(), contexto.codec()))
			.map(cartao -> MotivoNegacao.SENHA_INVALIDA);
	}
}
