package br.com.vr.miniautorizador.application;

import br.com.vr.miniautorizador.domain.exception.CartaoJaExistenteException;
import br.com.vr.miniautorizador.domain.model.Cartao;
import br.com.vr.miniautorizador.domain.model.NumeroCartao;
import br.com.vr.miniautorizador.domain.port.in.CriarCartaoUseCase;
import br.com.vr.miniautorizador.domain.port.out.CartaoRepository;
import br.com.vr.miniautorizador.domain.port.out.CodificadorSenha;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
public class CriarCartaoService implements CriarCartaoUseCase {

  private final CartaoRepository cartaoRepository;
  private final CodificadorSenha codec;

  @Override
  @Transactional
  public void criar(NumeroCartao numero, String senha) {
    Optional.of(numero)
        .filter(cartaoRepository::existe)
        .ifPresent(
            existente -> {
              throw new CartaoJaExistenteException(existente.valor(), senha);
            });
    cartaoRepository.salvar(Cartao.novo(numero, senha, codec));
  }
}
