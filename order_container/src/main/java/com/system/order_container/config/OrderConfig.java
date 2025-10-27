package com.system.order_container.config;

import com.system.order_application_service.handler.OrderCreateHandler;
import com.system.order_application_service.ports.OrderRepository;
import com.system.order_dataaccess.adapter.OrderRepositoryImpl;
import com.system.order_dataaccess.mapper.OrderDataMapper;
import com.system.order_dataaccess.repository.OrderJpaRepository;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "com.system.order_dataaccess.repository")
@ComponentScan(basePackages = {"com.system.order_dataaccess", "com.system.order_application_service"})
@EntityScan(basePackages = "com.system.order_dataaccess.entity") // Scan entities ở package dataaccess
public class OrderConfig {
    @Bean
    public OrderCreateHandler orderCreateHandler(OrderRepository orderRepository) {
        return new OrderCreateHandler(orderRepository);
    }

    @Bean
    public OrderRepository orderRepository(OrderJpaRepository orderJpaRepository, OrderDataMapper orderDataMapper) {
        return new OrderRepositoryImpl(orderJpaRepository, orderDataMapper);
    }
    @Bean
    public OrderDataMapper orderDataMapper() {
        return new OrderDataMapper();
    }
}