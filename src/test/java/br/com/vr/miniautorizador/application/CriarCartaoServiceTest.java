package br.com.vr.miniautorizador.application;

import br.com.vr.miniautorizador.domain.exception.CartaoJaExistenteException;
import br.com.vr.miniautorizador.domain.model.Dinheiro;
import br.com.vr.miniautorizador.domain.model.NumeroCartao;
import br.com.vr.miniautorizador.support.FakeCartaoRepository;
import br.com.vr.miniautorizador.support.PlainCodificadorSenha;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CriarCartaoServiceTest {

	private FakeCartaoRepository repository;
	private CriarCartaoService service;

	@BeforeEach
	void setUp() {
		repository = new FakeCartaoRepository();
		service = new CriarCartaoService(repository, new PlainCodificadorSenha());
	}

	@Test
	void criaCartaoComSaldoInicialDeQuinhentos() {
		var numero = NumeroCartao.de("6549873025634501");
		service.criar(numero, "1234");

		var persistido = repository.obrigatorio(numero);
		assertThat(persistido.saldo()).isEqualTo(Dinheiro.SALDO_INICIAL);
		assertThat(persistido.senhaHash()).isEqualTo("1234");
	}

	@Test
	void recusaSegundoCartaoComMesmoNumero() {
		var numero = NumeroCartao.de("6549873025634501");
		service.criar(numero, "1234");

		assertThatThrownBy(() -> service.criar(numero, "1234"))
			.isInstanceOf(CartaoJaExistenteException.class)
			.extracting(ex -> ((CartaoJaExistenteException) ex).numeroCartao())
			.isEqualTo("6549873025634501");
	}
}
