package com.example.playcation.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@RequiredArgsConstructor
@Configuration
public class SwaggerConfig {
  @Bean
  public OpenAPI openAPI() {
    io.swagger.v3.oas.models.info.Info info = new io.swagger.v3.oas.models.info.Info()
        .title("Playcation API Document")
        .version("v0.0.1")
        .description("Playcation의 API 명세서입니다.");

    SecurityScheme apiKey = new SecurityScheme()
        .scheme("Bearer")
        .bearerFormat("JWT")
        .type(SecurityScheme.Type.APIKEY)
        .in(SecurityScheme.In.HEADER)
        .name("Authorization");

    SecurityRequirement securityRequirement = new SecurityRequirement()
        .addList("Bearer ");

    return new OpenAPI()
        .components(new Components().addSecuritySchemes("Bearer ", apiKey))
        .addSecurityItem(securityRequirement);
  }
}
