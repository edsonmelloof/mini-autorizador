package br.com.vr.miniautorizador.application;

import br.com.vr.miniautorizador.domain.exception.CartaoNaoEncontradoException;
import br.com.vr.miniautorizador.domain.model.Dinheiro;
import br.com.vr.miniautorizador.domain.model.NumeroCartao;
import br.com.vr.miniautorizador.domain.port.in.ConsultarSaldoUseCase;
import br.com.vr.miniautorizador.domain.port.out.CartaoRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ConsultarSaldoService implements ConsultarSaldoUseCase {

  private final CartaoRepository cartaoRepository;

  @Override
  public Dinheiro consultar(NumeroCartao numero) {
    return cartaoRepository
        .buscarPorNumero(numero)
        .map(cartao -> cartao.saldo())
        .orElseThrow(() -> new CartaoNaoEncontradoException(numero.valor()));
  }
}
