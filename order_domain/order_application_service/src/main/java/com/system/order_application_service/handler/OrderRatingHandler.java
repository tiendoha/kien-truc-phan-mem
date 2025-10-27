package com.system.order_application_service.handler;

import com.system.order_application_service.dto.OrderRatingRequest;
import com.system.order_application_service.dto.OrderResponse;
import com.system.order_application_service.exception.OrderNotFoundException;
import com.system.order_application_service.ports.OrderRepository;
import com.system.order_domain_core.entity.Order;

import java.util.Objects;
import java.util.UUID;

public class OrderRatingHandler {

    private final OrderRepository orderRepository;
    private final OrderDtoMapper orderDtoMapper;

    public OrderRatingHandler(OrderRepository orderRepository) {
        this.orderRepository = Objects.requireNonNull(orderRepository, "OrderRepository cannot be null");
        this.orderDtoMapper = new OrderDtoMapper();
    }

    public OrderResponse rateOrder(UUID orderId, OrderRatingRequest request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Không tìm thấy đơn hàng: " + orderId));

        order.addRating(request.getRating(), request.getComment());

        Order savedOrder = orderRepository.save(order);

        return orderDtoMapper.mapDomainToDto(savedOrder);
    }
}