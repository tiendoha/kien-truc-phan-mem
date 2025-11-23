package com.system.order_application_service.handler;

import com.system.order_application_service.dto.CreateOrderRequest;
import com.system.order_application_service.dto.OrderResponse;
import com.system.order_application_service.ports.OrderRepository;
import com.system.order_application_service.ports.VoucherServicePort;
import com.system.order_domain_core.entity.Order;
import com.system.order_domain_core.exception.OrderDomainException;
import com.system.order_domain_core.valueobject.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

public class OrderCreateHandler {
    private final OrderRepository orderRepository;
    private final VoucherServicePort voucherServicePort;
    private final OrderDtoMapper orderDtoMapper;

    public OrderCreateHandler(OrderRepository orderRepository, VoucherServicePort voucherServicePort) {
        this.orderRepository = Objects.requireNonNull(orderRepository, "OrderRepository cannot be null");
        this.voucherServicePort = Objects.requireNonNull(voucherServicePort, "VoucherServicePort cannot be null");
        this.orderDtoMapper = new OrderDtoMapper();
    }

    public OrderResponse createOrder(CreateOrderRequest request) {
        // 1. Map DTO sang Domain (OrderItem)
        List<OrderItem> orderItems = request.getItems().stream()
                .map(item -> new OrderItem(
                        null, null,
                        item.getProductId(),
                        item.getPrice(),
                        item.getQuantity() != null ? item.getQuantity() : 0,
                        item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity() != null ? item.getQuantity() : 0))))
                .collect(Collectors.toList());

        // 2. Tạo Order (Sử dụng static factory method)
        Order order = Order.createOrder(
                new OrderId(UUID.randomUUID()),
                new CustomerId(request.getCustomerId()),
                new RestaurantId(request.getRestaurantId()),
                new TrackingId(UUID.randomUUID()),
                orderItems
        );

        // 3. (Feature 4) Xử lý Voucher (nếu có)
        if (request.getVoucherCode() != null && !request.getVoucherCode().isEmpty()) {
            // Gọi port
            Voucher voucher = voucherServicePort.validateAndGetVoucher(request.getVoucherCode())
                    .orElseThrow(() -> new OrderDomainException("Mã voucher không hợp lệ: " + request.getVoucherCode()));

            // Gọi logic domain
            order.applyVoucher(voucher);
        }

        // 4. Lưu vào DB
        Order savedOrder = orderRepository.save(order);
        // 5. Ánh xạ sang OrderResponse
        return orderDtoMapper.mapDomainToDto(savedOrder);
    }
}