package com.skillswap.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Swagger/OpenAPI Configuration for SkillSwap Backend
 * Provides API documentation and testing interface
 * 
 * @author SkillSwap Team
 */
@Configuration
public class SwaggerConfig {

    @Value("${server.servlet.context-path:/api/v1}")
    private String contextPath;

    @Value("${server.port:8080}")
    private String serverPort;

    @Bean
    public OpenAPI skillswapOpenAPI() {
        return new OpenAPI()
                .info(createApiInfo())
                .servers(createServers())
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes("bearerAuth", createSecurityScheme()));
    }

    private Info createApiInfo() {
        return new Info()
                .title("SkillSwap Backend API")
                .description("P2P Skill Exchange Platform - Backend REST API Documentation")
                .version("1.0.0")
                .contact(new Contact()
                        .name("SkillSwap Team")
                        .email("developers@skillswap.com")
                        .url("https://skillswap.com"))
                .license(new License()
                        .name("MIT License")
                        .url("https://opensource.org/licenses/MIT"));
    }

    private List<Server> createServers() {
        return List.of(
                new Server()
                        .url("http://localhost:" + serverPort + contextPath)
                        .description("Development Server"),
                new Server()
                        .url("https://api.skillswap.com/api/v1")
                        .description("Production Server"),
                new Server()
                        .url("https://staging-api.skillswap.com/api/v1")
                        .description("Staging Server")
        );
    }

    private SecurityScheme createSecurityScheme() {
        return new SecurityScheme()
                .name("bearerAuth")
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .description("JWT Authentication token. Format: Bearer {token}");
    }
}
