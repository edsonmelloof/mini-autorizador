package br.com.vr.miniautorizador.domain.port.out;

import br.com.vr.miniautorizador.domain.model.Cartao;
import br.com.vr.miniautorizador.domain.model.Dinheiro;
import br.com.vr.miniautorizador.domain.model.NumeroCartao;

import java.util.Optional;

public interface CartaoRepository {

	boolean existe(NumeroCartao numero);

	void salvar(Cartao cartao);

	Optional<Cartao> buscarPorNumero(NumeroCartao numero);

	int debitarSeHouverSaldo(NumeroCartao numero, Dinheiro valor);
}
