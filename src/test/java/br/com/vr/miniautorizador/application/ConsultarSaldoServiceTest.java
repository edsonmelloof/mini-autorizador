package br.com.vr.miniautorizador.application;

import br.com.vr.miniautorizador.domain.exception.CartaoNaoEncontradoException;
import br.com.vr.miniautorizador.domain.model.Cartao;
import br.com.vr.miniautorizador.domain.model.Dinheiro;
import br.com.vr.miniautorizador.domain.model.NumeroCartao;
import br.com.vr.miniautorizador.support.FakeCartaoRepository;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ConsultarSaldoServiceTest {

	@Test
	void devolveSaldoDoCartaoExistente() {
		var repository = new FakeCartaoRepository();
		var numero = NumeroCartao.de("6549873025634501");
		repository.salvar(Cartao.reconstituir(numero, "hash", Dinheiro.de("495.15")));

		Dinheiro saldo = new ConsultarSaldoService(repository).consultar(numero);

		assertThat(saldo.valor()).isEqualByComparingTo("495.15");
	}

	@Test
	void lancaQuandoCartaoNaoExiste() {
		var service = new ConsultarSaldoService(new FakeCartaoRepository());
		assertThatThrownBy(() -> service.consultar(NumeroCartao.de("0000")))
			.isInstanceOf(CartaoNaoEncontradoException.class);
	}
}
