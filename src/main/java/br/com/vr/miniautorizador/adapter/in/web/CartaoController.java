package br.com.vr.miniautorizador.adapter.in.web;

import br.com.vr.miniautorizador.adapter.in.web.dto.CartaoRequest;
import br.com.vr.miniautorizador.adapter.in.web.dto.CartaoResponse;
import br.com.vr.miniautorizador.domain.model.NumeroCartao;
import br.com.vr.miniautorizador.domain.port.in.ConsultarSaldoUseCase;
import br.com.vr.miniautorizador.domain.port.in.CriarCartaoUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
@Tag(name = "Cartões")
public class CartaoController {

	private final CriarCartaoUseCase criarCartao;
	private final ConsultarSaldoUseCase consultarSaldo;

	@PostMapping
	@Operation(summary = "Criar cartão com saldo inicial de R$ 500,00")
	@ApiResponses({
		@ApiResponse(
			responseCode = "201",
			description = "Criado",
			content = @Content(schema = @Schema(implementation = CartaoResponse.class))),
		@ApiResponse(
			responseCode = "422",
			description = "Cartão já existe — mesmo JSON do request",
			content = @Content(schema = @Schema(implementation = CartaoResponse.class))),
		@ApiResponse(responseCode = "401", description = "Sem Basic Auth válido", content = @Content)
	})
	public ResponseEntity<CartaoResponse> criar(@Valid @RequestBody CartaoRequest request) {
		criarCartao.criar(NumeroCartao.de(request.numeroCartao()), request.senha());
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(new CartaoResponse(request.senha(), request.numeroCartao()));
	}

	@GetMapping("/{numeroCartao}")
	@Operation(summary = "Consultar saldo do cartão")
	@ApiResponses({
		@ApiResponse(
			responseCode = "200",
			description = "Saldo numérico (não é objeto JSON)",
			content = @Content(
				schema = @Schema(type = "number", example = "500.00"),
				examples = @ExampleObject(value = "500.00"))),
		@ApiResponse(responseCode = "404", description = "Cartão inexistente — sem body", content = @Content),
		@ApiResponse(responseCode = "401", description = "Sem Basic Auth válido", content = @Content)
	})
	public ResponseEntity<BigDecimal> saldo(@PathVariable String numeroCartao) {
		return ResponseEntity.ok(consultarSaldo.consultar(NumeroCartao.de(numeroCartao)).valor());
	}
}
