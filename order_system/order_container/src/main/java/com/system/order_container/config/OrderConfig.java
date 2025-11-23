package com.system.order_container.config;

import com.system.order_application_service.handler.*;
import com.system.order_application_service.handler.OrderDtoMapper;

import com.system.order_application_service.ports.OrderRepository;
import com.system.order_application_service.ports.VoucherServicePort;
import com.system.order_dataaccess.adapter.OrderRepositoryImpl;
import com.system.order_dataaccess.mapper.OrderDataMapper;
import com.system.order_dataaccess.repository.OrderJpaRepository;
import jakarta.persistence.EntityManager;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import com.system.order_application_service.handler.PaymentResponseMessageListenerImpl;
import com.system.order_application_service.ports.input.PaymentResponseMessageListener;
@Configuration
@EnableJpaRepositories(basePackages = "com.system.order_dataaccess.repository")
@ComponentScan(basePackages = {"com.system.order_dataaccess", "com.system.order_application_service", "com.system.order_messaging"})
@EntityScan(basePackages = "com.system.order_dataaccess.entity")
public class OrderConfig {

    @Bean
    public OrderCreateHandler orderCreateHandler(OrderRepository orderRepository,
                                                 VoucherServicePort voucherServicePort) {
        return new OrderCreateHandler(orderRepository, voucherServicePort);
    }

    @Bean
    public OrderQueryHandler orderQueryHandler(OrderRepository orderRepository, OrderDtoMapper orderDtoMapper) {
        return new OrderQueryHandler(orderRepository, orderDtoMapper);
    }

    @Bean
    public OrderUpdateHandler orderUpdateHandler(OrderRepository orderRepository, VoucherServicePort voucherServicePort) {
        return new OrderUpdateHandler(orderRepository, voucherServicePort);
    }

    @Bean
    public OrderRatingHandler orderRatingHandler(OrderRepository orderRepository) {
        return new OrderRatingHandler(orderRepository);
    }

    @Bean
    public OrderStatisticsHandler orderStatisticsHandler(OrderRepository orderRepository) {
        return new OrderStatisticsHandler(orderRepository);
    }

    @Bean
    public OrderDtoMapper orderDtoMapper() {
        return new OrderDtoMapper();
    }

    @Bean
    public OrderRepository orderRepository(OrderJpaRepository orderJpaRepository,
                                           OrderDataMapper orderDataMapper,
                                           EntityManager entityManager) {
        return new OrderRepositoryImpl(orderJpaRepository, orderDataMapper, entityManager);
    }

    @Bean
    public OrderDataMapper orderDataMapper() {
        return new OrderDataMapper();
    }

    @Bean
    public PaymentResponseMessageListener paymentResponseMessageListener(OrderRepository orderRepository) {
        return new PaymentResponseMessageListenerImpl(orderRepository);
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/orders/**")
                        .allowedOrigins("http://localhost:3000")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true);

                // Also enable CORS for Swagger UI
                registry.addMapping("/swagger-ui/**")
                        .allowedOrigins("http://localhost:3000")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*");

                registry.addMapping("/v3/api-docs/**")
                        .allowedOrigins("http://localhost:3000")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*");
            }
        };
    }
}