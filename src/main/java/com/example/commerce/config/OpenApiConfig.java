package com.example.commerce.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";

        return new OpenAPI()
                .info(new Info()
                        .title("E-Commerce API")
                        .version("1.0.0")
                        .description("""
                                REST API for E-Commerce Application with JWT Authentication

                                **Default Test Users:**
                                - Admin: username=`admin`, password=`admin123` (Full access)
                                - Moderator: username=`moderator`, password=`mod123` (Manage products/categories)
                                - User: username=`user`, password=`user123` (Customer access)

                                **How to use:**
                                1. Login via `/api/auth/login` endpoint
                                2. Copy the JWT token from response
                                3. Click 'Authorize' button above
                                4. Enter: `Bearer <your-token>`
                                5. Click 'Authorize' and 'Close'
                                6. Now you can test protected endpoints!
                                """)
                        .contact(new Contact()
                                .name("API Support")
                                .email("support@commerce.example.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:3002")
                                .description("Development Server")
                ))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Enter JWT token (get it from /api/auth/login endpoint). Format: Bearer <token>")));
    }
}
