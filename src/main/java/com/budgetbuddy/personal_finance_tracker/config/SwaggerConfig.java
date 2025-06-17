package com.budgetbuddy.personal_finance_tracker.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Value("${app.version:1.0.0}")
    private String appVersion;

    @Value("${server.port:8080}")
    private String serverPort;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Personal Finance Tracker API")
                        .version(appVersion)
                        .description("""
                                A comprehensive REST API for personal finance management that allows users to:
                                - Track income and expenses with detailed categorization
                                - Set and monitor budgets across different categories
                                - Generate financial reports and analytics
                                - Manage transaction categories and subcategories
                                
                                Built with Spring Boot and secured with JWT authentication.
                                """)
                        .contact(new Contact()
                                .name("Finance Tracker Development Team")
                                .email("support@financetracker.com")
                                .url("https://financetracker.com/support"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:" + serverPort)
                                .description("Development Server"),
                        new Server()
                                .url("https://api.financetracker.com")
                                .description("Production Server"),
                        new Server()
                                .url("https://staging-api.financetracker.com")
                                .description("Staging Server")))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("JWT Authorization header using the Bearer scheme. Format: Bearer {token}")))
                .tags(List.of(
                        new Tag().name("Transactions").description("Operations related to financial transactions"),
                        new Tag().name("Budgets").description("Budget management and monitoring"),
                        new Tag().name("Categories").description("Transaction category management"),
                        new Tag().name("Reports").description("Financial reports and analytics"),
                        new Tag().name("Authentication").description("User authentication and authorization")
                ));
    }
}