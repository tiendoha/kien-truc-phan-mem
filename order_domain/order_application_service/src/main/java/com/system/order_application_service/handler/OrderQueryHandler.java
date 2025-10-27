package com.system.order_application_service.handler;

import com.system.order_application_service.dto.OrderResponse;
import com.system.order_application_service.ports.OrderRepository;
import com.system.order_domain_core.entity.Order;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

public class OrderQueryHandler {

    private final OrderRepository orderRepository;
    private final OrderDtoMapper orderDtoMapper;
    public OrderQueryHandler(OrderRepository orderRepository, OrderDtoMapper orderDtoMapper) {
        this.orderRepository = Objects.requireNonNull(orderRepository, "OrderRepository cannot be null");
        this.orderDtoMapper = Objects.requireNonNull(orderDtoMapper, "OrderDtoMapper cannot be null");
    }

    /**
     * Use case: Lấy lịch sử đơn hàng theo customerId.
     */
    public List<OrderResponse> getOrdersByCustomerId(UUID customerId) {
        // 1. Gọi port để lấy dữ liệu
        List<Order> orders = orderRepository.findByCustomerId(customerId);

        // 2. Map từ Domain Entity (Order) sang DTO (OrderResponse)
        return orders.stream()
                .map(orderDtoMapper::mapDomainToDto)
                .collect(Collectors.toList());
    }

}