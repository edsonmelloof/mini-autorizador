package br.com.vr.miniautorizador.domain.port.in;

import br.com.vr.miniautorizador.domain.model.Dinheiro;
import br.com.vr.miniautorizador.domain.model.NumeroCartao;

public interface ConsultarSaldoUseCase {

	Dinheiro consultar(NumeroCartao numero);
}
