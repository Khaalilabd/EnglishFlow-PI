package com.englishflow.auth.config;

import io.swagger.v3.oas.models.Components;
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

@Configuration
public class OpenApiConfig {

    @Value("${app.backend.url:http://localhost:8081}")
    private String backendUrl;

    @Value("${app.frontend.url:http://localhost:4200}")
    private String frontendUrl;

    @Bean
    public OpenAPI authServiceOpenAPI() {
        // Security scheme for JWT Bearer token
        SecurityScheme securityScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER)
                .name("Authorization")
                .description("JWT Bearer token authentication");

        // Security requirement
        SecurityRequirement securityRequirement = new SecurityRequirement()
                .addList("bearerAuth");

        return new OpenAPI()
                .info(new Info()
                        .title("EnglishFlow Authentication Service API")
                        .description("""
                                Complete authentication and authorization service for EnglishFlow platform.
                                
                                ## Features
                                - Email/Password authentication
                                - OAuth2 (Google) integration
                                - JWT token-based authentication
                                - Refresh token rotation
                                - Email verification
                                - Password reset
                                - Role-based access control (RBAC)
                                - Session management
                                - Audit logging
                                - Rate limiting
                                - Invitation system for staff
                                
                                ## Authentication
                                Most endpoints require a valid JWT token. Include it in the Authorization header:
                                ```
                                Authorization: Bearer <your-jwt-token>
                                ```
                                
                                ## Roles
                                - **STUDENT**: Regular students
                                - **TUTOR**: Teachers/tutors
                                - **ACADEMIC_OFFICE_AFFAIR**: Academic staff
                                - **ADMIN**: System administrators
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("EnglishFlow Team")
                                .email("support@englishflow.com")
                                .url(frontendUrl))
                        .license(new License()
                                .name("Proprietary")
                                .url(frontendUrl)))
                .servers(List.of(
                        new Server()
                                .url(backendUrl)
                                .description("Auth Service (Direct)"),
                        new Server()
                                .url("http://localhost:8080/api")
                                .description("API Gateway"),
                        new Server()
                                .url("https://api.englishflow.com")
                                .description("Production API Gateway")))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", securityScheme))
                .addSecurityItem(securityRequirement);
    }
}
