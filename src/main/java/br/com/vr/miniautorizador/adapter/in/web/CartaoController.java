package br.com.vr.miniautorizador.adapter.in.web;

import br.com.vr.miniautorizador.adapter.in.web.dto.CartaoRequest;
import br.com.vr.miniautorizador.adapter.in.web.dto.CartaoResponse;
import br.com.vr.miniautorizador.domain.model.NumeroCartao;
import br.com.vr.miniautorizador.domain.port.in.ConsultarSaldoUseCase;
import br.com.vr.miniautorizador.domain.port.in.CriarCartaoUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/cartoes")
public class CartaoController {

	private final CriarCartaoUseCase criarCartao;
	private final ConsultarSaldoUseCase consultarSaldo;

	public CartaoController(CriarCartaoUseCase criarCartao, ConsultarSaldoUseCase consultarSaldo) {
		this.criarCartao = criarCartao;
		this.consultarSaldo = consultarSaldo;
	}

	@PostMapping
	public ResponseEntity<CartaoResponse> criar(@Valid @RequestBody CartaoRequest request) {
		criarCartao.criar(NumeroCartao.de(request.numeroCartao()), request.senha());
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(new CartaoResponse(request.senha(), request.numeroCartao()));
	}

	@GetMapping("/{numeroCartao}")
	public ResponseEntity<BigDecimal> saldo(@PathVariable String numeroCartao) {
		return ResponseEntity.ok(consultarSaldo.consultar(NumeroCartao.de(numeroCartao)).valor());
	}
}
