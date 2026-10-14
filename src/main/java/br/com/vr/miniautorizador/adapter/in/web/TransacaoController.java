package br.com.vr.miniautorizador.adapter.in.web;

import br.com.vr.miniautorizador.adapter.in.web.dto.TransacaoRequest;
import br.com.vr.miniautorizador.domain.model.ResultadoAutorizacao;
import br.com.vr.miniautorizador.domain.port.in.AutorizarTransacaoUseCase;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "Transações")
public class TransacaoController {

	private final AutorizarTransacaoUseCase autorizarTransacao;

	@PostMapping(path = "/transacoes", produces = MediaType.TEXT_PLAIN_VALUE)
	@Operation(summary = "Autorizar transação e debitar o saldo")
	@ApiResponses({
		@ApiResponse(
			responseCode = "201",
			description = "Autorizada",
			content = @Content(
				mediaType = MediaType.TEXT_PLAIN_VALUE,
				schema = @Schema(type = "string"),
				examples = @ExampleObject(value = "OK"))),
		@ApiResponse(
			responseCode = "422",
			description = "Regra de autorização recusou",
			content = @Content(
				mediaType = MediaType.TEXT_PLAIN_VALUE,
				schema = @Schema(type = "string"),
				examples = {
					@ExampleObject(name = "saldo", value = "SALDO_INSUFICIENTE"),
					@ExampleObject(name = "senha", value = "SENHA_INVALIDA"),
					@ExampleObject(name = "cartao", value = "CARTAO_INEXISTENTE")
				})),
		@ApiResponse(responseCode = "401", description = "Sem Basic Auth válido", content = @Content)
	})
	public ResponseEntity<String> autorizar(@Valid @RequestBody TransacaoRequest request) {
		return switch (autorizarTransacao.autorizar(request.paraDominio())) {
			case ResultadoAutorizacao.Aprovada() -> ResponseEntity.status(HttpStatus.CREATED).body("OK");
			case ResultadoAutorizacao.Negada(var motivo) ->
				ResponseEntity.status(HttpStatus.UNPROCESSABLE_CONTENT).body(motivo.name());
		};
	}
}
