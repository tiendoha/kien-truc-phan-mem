package com.system.payment_container.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class PaymentConfig {
    // Configuration for payment service without Kafka messaging
    // Database repositories and services are auto-configured through
    // @SpringBootApplication(scanBasePackages = "com.system") and @EnableJpaRepositories
}