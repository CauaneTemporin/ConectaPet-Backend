package br.com.conectapet.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.Components;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Conecta PET API")
                .version("1.0.0")
                .description("API completa para sistema de adoção e doação de pets, apadrinhamientos e voluntariado")
                .contact(new Contact()
                    .name("Conecta PET")
                    .url("https://conectapet.com.br")))
            .components(new Components()
                .addSecuritySchemes("bearerAuth", new SecurityScheme()
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT")
                    .description("Copie o token JWT no formato: Bearer {token}")))
            .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }
}

