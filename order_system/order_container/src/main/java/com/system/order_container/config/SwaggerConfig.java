package com.system.order_container.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI orderServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Order Service API")
                        .description("RESTful API for Order Management System with microservices architecture. This service handles order lifecycle, items, vouchers, ratings, and statistics.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Order System Team")
                                .email("order-system@example.com")
                                .url("https://example.com/order-system"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8081")
                                .description("Local Development Server"),
                        new Server()
                                .url("http://localhost:8081")
                                .description("Alternative Local Port"),
                        new Server()
                                .url("https://api.example.com/orders")
                                .description("Production Server")
                ));
    }
}