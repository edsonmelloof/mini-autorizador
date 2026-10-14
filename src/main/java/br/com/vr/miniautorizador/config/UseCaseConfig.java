package br.com.vr.miniautorizador.config;

import br.com.vr.miniautorizador.application.AutorizarTransacaoService;
import br.com.vr.miniautorizador.application.ConsultarSaldoService;
import br.com.vr.miniautorizador.application.CriarCartaoService;
import br.com.vr.miniautorizador.domain.autorizacao.CadeiaRegrasAutorizacao;
import br.com.vr.miniautorizador.domain.autorizacao.CartaoExistenteRegra;
import br.com.vr.miniautorizador.domain.autorizacao.SaldoDisponivelRegra;
import br.com.vr.miniautorizador.domain.autorizacao.SenhaCorretaRegra;
import br.com.vr.miniautorizador.domain.port.in.AutorizarTransacaoUseCase;
import br.com.vr.miniautorizador.domain.port.in.ConsultarSaldoUseCase;
import br.com.vr.miniautorizador.domain.port.in.CriarCartaoUseCase;
import br.com.vr.miniautorizador.domain.port.out.CartaoRepository;
import br.com.vr.miniautorizador.domain.port.out.CodificadorSenha;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.List;

@Configuration
@EnableTransactionManagement(proxyTargetClass = true)
public class UseCaseConfig {

	@Bean
	CadeiaRegrasAutorizacao cadeiaRegrasAutorizacao() {
		return new CadeiaRegrasAutorizacao(List.of(
			new CartaoExistenteRegra(),
			new SenhaCorretaRegra(),
			new SaldoDisponivelRegra()
		));
	}

	@Bean
	CriarCartaoUseCase criarCartaoUseCase(CartaoRepository cartaoRepository, CodificadorSenha codec) {
		return new CriarCartaoService(cartaoRepository, codec);
	}

	@Bean
	ConsultarSaldoUseCase consultarSaldoUseCase(CartaoRepository cartaoRepository) {
		return new ConsultarSaldoService(cartaoRepository);
	}

	@Bean
	AutorizarTransacaoUseCase autorizarTransacaoUseCase(
		CartaoRepository cartaoRepository,
		CodificadorSenha codec,
		CadeiaRegrasAutorizacao cadeia
	) {
		return new AutorizarTransacaoService(cartaoRepository, codec, cadeia);
	}
}
