package br.com.vr.miniautorizador.adapter.in.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class MiniAutorizadorApiTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void fluxoDoContratoDaAvaliacao() throws Exception {
		String numero = numeroUnico();

		mockMvc.perform(post("/cartoes")
				.with(httpBasic("username", "password"))
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{"numeroCartao":"%s","senha":"1234"}
					""".formatted(numero)))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.senha").value("1234"))
			.andExpect(jsonPath("$.numeroCartao").value(numero));

		mockMvc.perform(get("/cartoes/{numero}", numero)
				.with(httpBasic("username", "password")))
			.andExpect(status().isOk())
			.andExpect(content().string("500.00"));

		mockMvc.perform(post("/cartoes")
				.with(httpBasic("username", "password"))
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{"numeroCartao":"%s","senha":"1234"}
					""".formatted(numero)))
			.andExpect(status().isUnprocessableEntity())
			.andExpect(jsonPath("$.senha").value("1234"))
			.andExpect(jsonPath("$.numeroCartao").value(numero));

		mockMvc.perform(post("/transacoes")
				.with(httpBasic("username", "password"))
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{"numeroCartao":"%s","senhaCartao":"1234","valor":10.00}
					""".formatted(numero)))
			.andExpect(status().isCreated())
			.andExpect(content().string("OK"));

		mockMvc.perform(get("/cartoes/{numero}", numero)
				.with(httpBasic("username", "password")))
			.andExpect(status().isOk())
			.andExpect(content().string("490.00"));

		for (int i = 0; i < 49; i++) {
			mockMvc.perform(post("/transacoes")
					.with(httpBasic("username", "password"))
					.contentType(MediaType.APPLICATION_JSON)
					.content("""
						{"numeroCartao":"%s","senhaCartao":"1234","valor":10.00}
						""".formatted(numero)))
				.andExpect(status().isCreated());
		}

		mockMvc.perform(post("/transacoes")
				.with(httpBasic("username", "password"))
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{"numeroCartao":"%s","senhaCartao":"1234","valor":10.00}
					""".formatted(numero)))
			.andExpect(status().isUnprocessableEntity())
			.andExpect(content().string("SALDO_INSUFICIENTE"));

		mockMvc.perform(post("/transacoes")
				.with(httpBasic("username", "password"))
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{"numeroCartao":"%s","senhaCartao":"9999","valor":1.00}
					""".formatted(numero)))
			.andExpect(status().isUnprocessableEntity())
			.andExpect(content().string("SENHA_INVALIDA"));

		mockMvc.perform(post("/transacoes")
				.with(httpBasic("username", "password"))
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{"numeroCartao":"%s","senhaCartao":"1234","valor":1.00}
					""".formatted(numeroUnico())))
			.andExpect(status().isUnprocessableEntity())
			.andExpect(content().string("CARTAO_INEXISTENTE"));
	}

	@Test
	void saldoDeCartaoInexistenteRetorna404() throws Exception {
		mockMvc.perform(get("/cartoes/{numero}", numeroUnico())
				.with(httpBasic("username", "password")))
			.andExpect(status().isNotFound())
			.andExpect(content().string(""));
	}

	@Test
	void semAutenticacaoRetorna401() throws Exception {
		mockMvc.perform(get("/cartoes/6549873025634501"))
			.andExpect(status().isUnauthorized());
	}

	private String numeroUnico() {
		return UUID.randomUUID().toString().replace("-", "").substring(0, 16);
	}
}
