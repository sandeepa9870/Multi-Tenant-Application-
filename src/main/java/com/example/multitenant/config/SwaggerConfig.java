package com.example.multitenant.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Multi-Tenant E-Commerce SaaS API")
                        .version("1.0.0")
                        .description("Production-ready Authentication Module for Multi-Tenant E-Commerce SaaS Application")
                        .contact(new Contact()
                                .name("Development Team")
                                .email("dev@multitenant.com")
                        )
                )
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes("Bearer Authentication",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("JWT access token for API authentication")
                        )
                );
    }
}

