package br.com.vr.miniautorizador.adapter.in.web;

import br.com.vr.miniautorizador.adapter.in.web.dto.TransacaoRequest;
import br.com.vr.miniautorizador.domain.model.ResultadoAutorizacao;
import br.com.vr.miniautorizador.domain.port.in.AutorizarTransacaoUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TransacaoController {

	private final AutorizarTransacaoUseCase autorizarTransacao;

	public TransacaoController(AutorizarTransacaoUseCase autorizarTransacao) {
		this.autorizarTransacao = autorizarTransacao;
	}

	@PostMapping(path = "/transacoes", produces = MediaType.TEXT_PLAIN_VALUE)
	public ResponseEntity<String> autorizar(@Valid @RequestBody TransacaoRequest request) {
		return switch (autorizarTransacao.autorizar(request.paraDominio())) {
			case ResultadoAutorizacao.Aprovada ignored -> ResponseEntity.status(HttpStatus.CREATED).body("OK");
			case ResultadoAutorizacao.Negada negada -> ResponseEntity
				.status(HttpStatus.UNPROCESSABLE_ENTITY)
				.body(negada.motivo().name());
		};
	}
}
