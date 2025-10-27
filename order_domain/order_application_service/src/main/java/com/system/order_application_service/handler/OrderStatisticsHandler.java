package com.system.order_application_service.handler;

import com.system.order_application_service.dto.OrderStatisticsResponse;
import com.system.order_application_service.ports.OrderRepository;
import com.system.order_domain_core.valueobject.OrderStatistics;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.UUID;

public class OrderStatisticsHandler {

    private final OrderRepository orderRepository;

    public OrderStatisticsHandler(OrderRepository orderRepository) {
        this.orderRepository = Objects.requireNonNull(orderRepository, "OrderRepository cannot be null");
    }

    public OrderStatisticsResponse getStatistics(UUID customerId, LocalDate startDate, LocalDate endDate) {

        // Chuyển đổi LocalDate sang ZonedDateTime (Vì CSDL dùng ZonedDateTime)
        ZonedDateTime startDateTime = (startDate != null) ? startDate.atStartOfDay(ZoneId.systemDefault()) : null;
        ZonedDateTime endDateTime = (endDate != null) ? endDate.atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()) : null;

        // 1. Gọi Port
        OrderStatistics stats = orderRepository.getStatistics(customerId, startDateTime, endDateTime);

        // 2. Map sang DTO
        return new OrderStatisticsResponse(
                stats.getTotalOrders(),
                stats.getTotalRevenue(),
                stats.getAverageOrderValue()
        );
    }
}