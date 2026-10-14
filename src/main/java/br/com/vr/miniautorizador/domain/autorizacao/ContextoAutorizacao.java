package br.com.vr.miniautorizador.domain.autorizacao;

import br.com.vr.miniautorizador.domain.model.Cartao;
import br.com.vr.miniautorizador.domain.model.Transacao;
import br.com.vr.miniautorizador.domain.port.out.CodificadorSenha;

import java.util.Optional;

public record ContextoAutorizacao(
	Optional<Cartao> cartao,
	Transacao transacao,
	CodificadorSenha codec
) {
}
