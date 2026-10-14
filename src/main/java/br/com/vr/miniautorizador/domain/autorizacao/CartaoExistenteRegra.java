package br.com.vr.miniautorizador.domain.autorizacao;

import br.com.vr.miniautorizador.domain.model.MotivoNegacao;

import java.util.Optional;

public final class CartaoExistenteRegra implements RegraAutorizacao {

	@Override
	public Optional<MotivoNegacao> avaliar(ContextoAutorizacao contexto) {
		return contexto.cartao()
			.map(cartao -> Optional.<MotivoNegacao>empty())
			.orElseGet(() -> Optional.of(MotivoNegacao.CARTAO_INEXISTENTE));
	}
}
