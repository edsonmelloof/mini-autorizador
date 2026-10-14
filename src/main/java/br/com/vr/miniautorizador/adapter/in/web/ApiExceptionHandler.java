package br.com.vr.miniautorizador.adapter.in.web;

import br.com.vr.miniautorizador.adapter.in.web.dto.CartaoResponse;
import br.com.vr.miniautorizador.domain.exception.CartaoJaExistenteException;
import br.com.vr.miniautorizador.domain.exception.CartaoNaoEncontradoException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

	@ExceptionHandler(CartaoJaExistenteException.class)
	public ResponseEntity<CartaoResponse> cartaoJaExistente(CartaoJaExistenteException ex) {
		return ResponseEntity.status(HttpStatus.UNPROCESSABLE_CONTENT)
			.body(new CartaoResponse(ex.senha(), ex.numeroCartao()));
	}

	@ExceptionHandler(CartaoNaoEncontradoException.class)
	public ResponseEntity<Void> cartaoNaoEncontrado() {
		return ResponseEntity.notFound().build();
	}
}
