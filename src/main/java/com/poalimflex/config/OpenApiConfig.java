package com.poalimflex.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Poalim Flex API",
                version = "1.0.0",
                description = "Flexible Mortgage Repayment Solution API",
                contact = @Contact(
                        name = "Poalim Flex Development Team",
                        email = "dev@poalimflex.com"
                )
        ),
        servers = {
                @Server(url = "http://localhost:8080", description = "Local Development Server"),
                @Server(url = "https://api.poalimflex.com", description = "Production Server")
        }
)
public class OpenApiConfig {
    @Bean
    public GroupedOpenApi mortgageApi() {
        return GroupedOpenApi.builder()
                .group("mortgage")
                .pathsToMatch("/api/mortgage/**")
                .build();
    }

    @Bean
    public GroupedOpenApi userApi() {
        return GroupedOpenApi.builder()
                .group("user")
                .pathsToMatch("/api/user/**")
                .build();
    }
}