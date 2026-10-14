package br.com.vr.miniautorizador.adapter.in.web;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

	@Bean
	OpenAPI miniAutorizadorOpenAPI() {
		var basic = new SecurityScheme()
			.type(SecurityScheme.Type.HTTP)
			.scheme("basic");
		return new OpenAPI()
			.info(new Info()
				.title("Mini autorizador")
				.version("1.0")
				.description("API do teste VR Benefícios. Contratos REST com HTTP Basic (`username` / `password`)."))
			.components(new Components().addSecuritySchemes("basic", basic))
			.addSecurityItem(new SecurityRequirement().addList("basic"));
	}
}
