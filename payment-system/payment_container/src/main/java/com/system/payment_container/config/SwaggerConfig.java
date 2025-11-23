package com.system.payment_container.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI paymentServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Payment Service API")
                        .description("RESTful API for Payment and Credit Management System")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Payment Service Team")
                                .email("support@payment-system.com")
                                .url("https://payment-system.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8082")
                                .description("Development server"),
                        new Server()
                                .url("https://api.payment-system.com")
                                .description("Production server")
                ))
                .tags(List.of(
                        new Tag()
                                .name("Credit Management")
                                .description("APIs for managing customer credits"),
                        new Tag()
                                .name("Health Check")
                                .description("Service health monitoring endpoints")
                ));
    }
}