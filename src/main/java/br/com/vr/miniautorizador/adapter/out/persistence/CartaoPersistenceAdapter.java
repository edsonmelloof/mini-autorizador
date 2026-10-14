package br.com.vr.miniautorizador.adapter.out.persistence;

import br.com.vr.miniautorizador.domain.model.Cartao;
import br.com.vr.miniautorizador.domain.model.Dinheiro;
import br.com.vr.miniautorizador.domain.model.NumeroCartao;
import br.com.vr.miniautorizador.domain.port.out.CartaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional
@RequiredArgsConstructor
public class CartaoPersistenceAdapter implements CartaoRepository {

  private final SpringDataCartaoRepository springData;

  @Override
  public boolean existe(NumeroCartao numero) {
    return springData.existsByNumeroCartao(numero.valor());
  }

  @Override
  public void salvar(Cartao cartao) {
    springData.save(
        new CartaoJpaEntity(cartao.numero().valor(), cartao.senhaHash(), cartao.saldo().valor()));
  }

  @Override
  public Optional<Cartao> buscarPorNumero(NumeroCartao numero) {
    return springData.findByNumeroCartao(numero.valor()).map(this::paraDominio);
  }

  @Override
  public int debitarSeHouverSaldo(NumeroCartao numero, Dinheiro valor) {
    return springData.debitarSeHouverSaldo(numero.valor(), valor.valor());
  }

  private Cartao paraDominio(CartaoJpaEntity entity) {
    return Cartao.reconstituir(
        NumeroCartao.de(entity.getNumeroCartao()),
        entity.getSenhaHash(),
        Dinheiro.de(entity.getSaldo()));
  }
}
