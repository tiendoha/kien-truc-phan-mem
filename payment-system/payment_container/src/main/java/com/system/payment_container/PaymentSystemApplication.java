package com.system.payment_container;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories(basePackages = "com.system.payment_dataaccess.repository")
@EntityScan(basePackages = "com.system.payment_dataaccess.entity")
@SpringBootApplication(scanBasePackages = "com.system")
public class PaymentSystemApplication {
    public static void main(String[] args) {
        SpringApplication.run(PaymentSystemApplication.class, args);
    }
}