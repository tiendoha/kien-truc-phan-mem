package com.system.order_application_service.handler;

import com.system.order_application_service.dto.CreateOrderRequest;
import com.system.order_application_service.dto.OrderItemResponse;
import com.system.order_application_service.dto.OrderResponse;
import com.system.order_application_service.ports.OrderRepository;
import com.system.order_domain_core.entity.Order;
import com.system.order_domain_core.valueobject.CustomerId;
import com.system.order_domain_core.valueobject.OrderId;
import com.system.order_domain_core.valueobject.OrderItem;
import com.system.order_domain_core.valueobject.OrderStatus;
import com.system.order_domain_core.valueobject.RestaurantId;
import com.system.order_domain_core.valueobject.TrackingId;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

public class OrderCreateHandler {
    private final OrderRepository orderRepository;

    public OrderCreateHandler(OrderRepository orderRepository) {
        this.orderRepository = Objects.requireNonNull(orderRepository, "OrderRepository cannot be null");
    }

    public OrderResponse createOrder(CreateOrderRequest request) {
        List<OrderItem> orderItems = request.getItems().stream()
                .map(item -> new OrderItem(
                        null, // id có thể null nếu auto-increment
                        null, // orderId có thể gán sau
                        item.getProductId(),
                        item.getPrice(), // Giả sử getPrice() trả BigDecimal
                        item.getQuantity() != null ? item.getQuantity() : 0,
                        item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity() != null ? item.getQuantity() : 0))))
                .collect(Collectors.toList());

        BigDecimal totalPrice = orderItems.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity() != null ? item.getQuantity() : 0)))
                .reduce(BigDecimal.ZERO, BigDecimal::add); // Sử dụng BigDecimal arithmetic

        // Tạo và lưu Order
        Order order = new Order(
                new OrderId(UUID.randomUUID()),
                new CustomerId(request.getCustomerId()),
                new RestaurantId(request.getRestaurantId()),
                new TrackingId(UUID.randomUUID()),
                totalPrice, // Bây giờ là BigDecimal
                OrderStatus.PENDING,
                orderItems,
                null,
                ZonedDateTime.now()
        );

        Order savedOrder = orderRepository.save(order);

        // Ánh xạ sang OrderResponse
        return new OrderResponse(
                savedOrder.getId().getValue(),
                savedOrder.getCustomerId().getValue(),
                savedOrder.getRestaurantId().getValue(),
                savedOrder.getTrackingId().getValue(),
                savedOrder.getPrice(), // BigDecimal
                savedOrder.getOrderStatus().toString(),
                savedOrder.getItems().stream()
                        .map(item -> new OrderItemResponse(
                                item.getId(),
                                item.getOrderId(),
                                item.getProductId(),
                                item.getPrice(), // BigDecimal
                                item.getQuantity(),
                                item.getSubTotal())) // BigDecimal
                        .collect(Collectors.toList()),
                savedOrder.getFailureMessages(),
                savedOrder.getCreatedAt()
        );
    }
}