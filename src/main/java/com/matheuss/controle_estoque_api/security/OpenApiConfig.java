package com.matheuss.controle_estoque_api.security;


import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List; // Importe a classe List

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";

        return new OpenAPI()
                // 1. Adiciona a definição do esquema de segurança (isso estava correto).
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName, new SecurityScheme()
                                .name(securitySchemeName)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                        )
                )
                // 2. Adiciona o requisito de segurança globalmente (SINTAXE CORRIGIDA).
                .security(List.of(new SecurityRequirement().addList(securitySchemeName)))
                
                // 3. Adiciona informações gerais sobre a API.
                .info(new Info().title("Controle de Estoque API").version("v1"));
    }
}