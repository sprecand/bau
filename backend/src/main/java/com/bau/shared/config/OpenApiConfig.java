package com.bau.shared.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI configuration for spec-first approach.
 * Configures OpenAPI and Swagger UI properly.
 */
@Configuration
public class OpenApiConfig {
    
    /**
     * Custom OpenAPI configuration.
     * 
     * @return the OpenAPI configuration
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Bau Platform API")
                        .description("API for the Bau construction worker matching platform")
                        .version("1.0.0-SNAPSHOT")
                        .contact(new Contact()
                                .name("Bau Development Team")))
                .servers(List.of(
                        new io.swagger.v3.oas.models.servers.Server()
                                .url("http://localhost:8080")
                                .description("Local development server")
                ));
    }
} 