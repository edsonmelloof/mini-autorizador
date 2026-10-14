package br.com.vr.miniautorizador.domain.port.in;

import br.com.vr.miniautorizador.domain.model.NumeroCartao;

public interface CriarCartaoUseCase {

	void criar(NumeroCartao numero, String senha);
}
