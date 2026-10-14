package br.com.vr.miniautorizador.application;

import br.com.vr.miniautorizador.domain.autorizacao.CadeiaRegrasAutorizacao;
import br.com.vr.miniautorizador.domain.autorizacao.ContextoAutorizacao;
import br.com.vr.miniautorizador.domain.model.MotivoNegacao;
import br.com.vr.miniautorizador.domain.model.ResultadoAutorizacao;
import br.com.vr.miniautorizador.domain.model.Transacao;
import br.com.vr.miniautorizador.domain.port.in.AutorizarTransacaoUseCase;
import br.com.vr.miniautorizador.domain.port.out.CartaoRepository;
import br.com.vr.miniautorizador.domain.port.out.CodificadorSenha;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
public class AutorizarTransacaoService implements AutorizarTransacaoUseCase {

  private final CartaoRepository cartaoRepository;
  private final CodificadorSenha codec;
  private final CadeiaRegrasAutorizacao cadeia;

  @Override
  @Transactional
  public ResultadoAutorizacao autorizar(Transacao transacao) {
    var contexto =
        new ContextoAutorizacao(
            cartaoRepository.buscarPorNumero(transacao.numeroCartao()), transacao, codec);
    return switch (cadeia.avaliar(contexto)) {
      case ResultadoAutorizacao.Negada(var motivo) -> ResultadoAutorizacao.negada(motivo);
      case ResultadoAutorizacao.Aprovada() -> debitarAtomicamente(transacao);
    };
  }

  private ResultadoAutorizacao debitarAtomicamente(Transacao transacao) {
    int linhas = cartaoRepository.debitarSeHouverSaldo(transacao.numeroCartao(), transacao.valor());
    return Optional.of(linhas)
        .filter(atualizadas -> atualizadas == 1)
        .map(ok -> ResultadoAutorizacao.aprovada())
        .orElseGet(() -> ResultadoAutorizacao.negada(MotivoNegacao.SALDO_INSUFICIENTE));
  }
}
