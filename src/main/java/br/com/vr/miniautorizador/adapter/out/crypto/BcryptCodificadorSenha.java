package br.com.vr.miniautorizador.adapter.out.crypto;

import br.com.vr.miniautorizador.domain.port.out.CodificadorSenha;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class BcryptCodificadorSenha implements CodificadorSenha {

  private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

  @Override
  public String codificar(String senhaPlana) {
    return encoder.encode(senhaPlana);
  }

  @Override
  public boolean confere(String senhaPlana, String senhaHash) {
    return encoder.matches(senhaPlana, senhaHash);
  }
}
