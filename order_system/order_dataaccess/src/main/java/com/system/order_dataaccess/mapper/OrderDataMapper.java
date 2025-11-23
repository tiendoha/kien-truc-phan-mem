package com.system.order_dataaccess.mapper;

import com.system.order_dataaccess.entity.OrderEntity;
import com.system.order_dataaccess.entity.OrderItemEntity;
import com.system.order_domain_core.entity.Order;
import com.system.order_domain_core.valueobject.*; // Cập nhật import

import java.util.List;
import java.util.stream.Collectors;

public class OrderDataMapper {
    public OrderEntity orderToOrderEntity(Order order) {
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setId(order.getId().getValue());
        orderEntity.setCustomerId(order.getCustomerId().getValue());
        orderEntity.setRestaurantId(order.getRestaurantId().getValue());
        orderEntity.setTrackingId(order.getTrackingId().getValue());

        orderEntity.setPrice(order.getPrice()); // Giá cuối
        orderEntity.setOriginalPrice(order.getOriginalPrice()); // Giá gốc
        orderEntity.setDiscount(order.getDiscount());
        orderEntity.setVoucherCode(order.getVoucherCode());
        orderEntity.setRating(order.getRating());
        orderEntity.setComment(order.getComment());

        orderEntity.setOrderStatus(com.system.order_dataaccess.entity.OrderStatus.valueOf(order.getOrderStatus().toString()));
        orderEntity.setFailureMessages(order.getFailureMessages());
        orderEntity.setCreatedAt(order.getCreatedAt());

        orderEntity.setItems(order.getItems().stream()
                .map(item -> {
                    OrderItemEntity itemEntity = new OrderItemEntity();
                    itemEntity.setId(item.getId());
                    itemEntity.setOrderId(order.getId().getValue());
                    itemEntity.setProductId(item.getProductId());
                    itemEntity.setPrice(item.getPrice());
                    itemEntity.setQuantity(item.getQuantity());
                    itemEntity.setSubTotal(item.getSubTotal());
                    itemEntity.setOrder(orderEntity);
                    return itemEntity;
                }).collect(Collectors.toList()));
        return orderEntity;
    }

    public Order orderEntityToOrder(OrderEntity orderEntity) {
        return new Order(
                new OrderId(orderEntity.getId()),
                new CustomerId(orderEntity.getCustomerId()),
                new RestaurantId(orderEntity.getRestaurantId()),
                new TrackingId(orderEntity.getTrackingId()),
                orderEntity.getPrice(), // Giá cuối
                OrderStatus.valueOf(orderEntity.getOrderStatus()),
                orderEntity.getItems().stream()
                        .map(item -> new OrderItem(
                                item.getId(),
                                item.getOrderId(),
                                item.getProductId(),
                                item.getPrice(),
                                item.getQuantity(),
                                item.getSubTotal()))
                        .collect(Collectors.toList()),
                orderEntity.getFailureMessages(),
                orderEntity.getCreatedAt(),
                orderEntity.getOriginalPrice(),
                orderEntity.getDiscount(),
                orderEntity.getVoucherCode(),
                orderEntity.getRating(),
                orderEntity.getComment()
        );
    }
}