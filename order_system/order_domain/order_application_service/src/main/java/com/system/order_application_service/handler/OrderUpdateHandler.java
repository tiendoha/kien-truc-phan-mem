package com.system.order_application_service.handler;

import com.system.order_application_service.dto.OrderItemRequest;
import com.system.order_application_service.dto.OrderResponse;
import com.system.order_application_service.dto.UpdateOrderRequest;
import com.system.order_application_service.exception.OrderNotFoundException;
import com.system.order_application_service.ports.OrderRepository;
import com.system.order_application_service.ports.VoucherServicePort;
import com.system.order_domain_core.entity.Order;
import com.system.order_domain_core.exception.OrderDomainException;
import com.system.order_domain_core.valueobject.OrderItem;
import com.system.order_domain_core.valueobject.Voucher;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

public class OrderUpdateHandler {

    private final OrderRepository orderRepository;
    private final VoucherServicePort voucherServicePort; // <-- THÊM PORT
    private final OrderDtoMapper orderDtoMapper; // <-- DÙNG MAPPER CHUNG

    public OrderUpdateHandler(OrderRepository orderRepository, VoucherServicePort voucherServicePort) {
        this.orderRepository = Objects.requireNonNull(orderRepository, "OrderRepository cannot be null");
        this.voucherServicePort = Objects.requireNonNull(voucherServicePort, "VoucherServicePort cannot be null");
        this.orderDtoMapper = new OrderDtoMapper();
    }

    public OrderResponse updateOrder(UUID orderId, UpdateOrderRequest request) {
        // 1. Lấy Order
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Không tìm thấy đơn hàng: " + orderId));

        // 2. Map DTO (Request) sang Domain Entity (OrderItem)
        List<OrderItem> newOrderItems = mapDtoToDomain(request.getItems());

        // 3. Gọi logic nghiệp vụ của Domain
        order.updateItems(newOrderItems); // Cập nhật món hàng VÀ tính lại giá gốc

        // 4. (Feature 4) Xử lý Voucher (nếu có)
        if (request.getVoucherCode() != null && !request.getVoucherCode().isEmpty()) {
            Voucher voucher = voucherServicePort.validateAndGetVoucher(request.getVoucherCode())
                    .orElseThrow(() -> new OrderDomainException("Mã voucher không hợp lệ: " + request.getVoucherCode()));
            order.applyVoucher(voucher);
        }

        // 5. Lưu trạng thái mới
        Order updatedOrder = orderRepository.save(order);

        // 6. Map sang DTO (Response)
        return orderDtoMapper.mapDomainToDto(updatedOrder);
    }

    // --- Helper Methods ---
    private List<OrderItem> mapDtoToDomain(List<OrderItemRequest> requestItems) {
        return requestItems.stream()
                .map(item -> new OrderItem(
                        null, null,
                        item.getProductId(),
                        item.getPrice(),
                        item.getQuantity() != null ? item.getQuantity() : 0,
                        item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity() != null ? item.getQuantity() : 0))))
                .collect(Collectors.toList());
    }
}