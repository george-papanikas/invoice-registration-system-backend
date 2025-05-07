package com.github.georgepapanikas.invoiceregistrationsystem.configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for generating the OpenAPI specification and Swagger UI.
 *
 * <p>Defines API metadata (title, version, description) and
 * registers the JWT Bearer security scheme.</p>
 */
@Configuration
public class OpenAPIConfig {

    /**
     * Creates the main OpenAPI bean with API info and security schemes.
     *
     * @return the configured {@link OpenAPI} instance
     */
    @Bean
    public OpenAPI invoiceApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Invoice Registration System API")
                        .version("v1.0")
                )
                .components(new Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                        )
                )
                // Apply this scheme globally (to all operations that have no explicit override)
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }
}

