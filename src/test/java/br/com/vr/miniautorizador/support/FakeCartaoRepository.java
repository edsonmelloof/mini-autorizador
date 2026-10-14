package br.com.vr.miniautorizador.support;

import br.com.vr.miniautorizador.domain.model.Cartao;
import br.com.vr.miniautorizador.domain.model.Dinheiro;
import br.com.vr.miniautorizador.domain.model.NumeroCartao;
import br.com.vr.miniautorizador.domain.port.out.CartaoRepository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class FakeCartaoRepository implements CartaoRepository {

	private final Map<String, Cartao> cartoes = new ConcurrentHashMap<>();

	@Override
	public boolean existe(NumeroCartao numero) {
		return cartoes.containsKey(numero.valor());
	}

	@Override
	public void salvar(Cartao cartao) {
		cartoes.put(cartao.numero().valor(), cartao);
	}

	@Override
	public Optional<Cartao> buscarPorNumero(NumeroCartao numero) {
		return Optional.ofNullable(cartoes.get(numero.valor()));
	}

	@Override
	public int debitarSeHouverSaldo(NumeroCartao numero, Dinheiro valor) {
		synchronized (cartoes) {
			Cartao atual = cartoes.get(numero.valor());
			return Optional.ofNullable(atual)
				.filter(cartao -> cartao.possuiSaldo(valor))
				.map(cartao -> {
					cartoes.put(numero.valor(), Cartao.reconstituir(
						cartao.numero(),
						cartao.senhaHash(),
						cartao.saldo().subtrair(valor)
					));
					return 1;
				})
				.orElse(0);
		}
	}

	public Cartao obrigatorio(NumeroCartao numero) {
		return buscarPorNumero(numero).orElseThrow();
	}
}
