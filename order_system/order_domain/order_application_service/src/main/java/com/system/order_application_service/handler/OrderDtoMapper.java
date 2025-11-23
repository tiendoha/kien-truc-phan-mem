package com.system.order_application_service.handler;

import com.system.order_application_service.dto.OrderItemResponse;
import com.system.order_application_service.dto.OrderResponse;
import com.system.order_domain_core.entity.Order;

import java.util.stream.Collectors;

/**
 * Helper class để map giữa Domain và DTO
 */
public class OrderDtoMapper {

    public OrderResponse mapDomainToDto(Order order) {
        return new OrderResponse(
                order.getId().getValue(),
                order.getCustomerId().getValue(),
                order.getRestaurantId().getValue(),
                order.getTrackingId().getValue(),
                order.getPrice(), // Giá cuối
                order.getOrderStatus().toString(),
                order.getItems().stream()
                        .map(item -> new OrderItemResponse(
                                item.getId(),
                                item.getOrderId(),
                                item.getProductId(),
                                item.getPrice(),
                                item.getQuantity(),
                                item.getSubTotal()))
                        .collect(Collectors.toList()),
                order.getFailureMessages(),
                order.getCreatedAt(),
                order.getOriginalPrice(),
                order.getDiscount(),
                order.getVoucherCode(),
                order.getRating(),
                order.getComment()
        );
    }
}