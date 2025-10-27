package com.system.order_dataaccess.mapper;

import com.system.order_dataaccess.entity.OrderEntity;
import com.system.order_dataaccess.entity.OrderItemEntity;
import com.system.order_domain_core.entity.Order;
import com.system.order_domain_core.valueobject.CustomerId;
import com.system.order_domain_core.valueobject.OrderId;
import com.system.order_domain_core.valueobject.OrderItem;
import com.system.order_domain_core.valueobject.OrderStatus;
import com.system.order_domain_core.valueobject.RestaurantId;
import com.system.order_domain_core.valueobject.TrackingId;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class OrderDataMapper {
    public OrderEntity orderToOrderEntity(Order order) {
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setId(order.getId().getValue());
        orderEntity.setCustomerId(order.getCustomerId().getValue());
        orderEntity.setRestaurantId(order.getRestaurantId().getValue());
        orderEntity.setTrackingId(order.getTrackingId().getValue());
        orderEntity.setPrice(order.getPrice()); // Sử dụng float
        orderEntity.setOrderStatus(com.system.order_dataaccess.entity.OrderStatus.valueOf(order.getOrderStatus().toString()));
        orderEntity.setFailureMessages(order.getFailureMessages());
        orderEntity.setCreatedAt(order.getCreatedAt());
        orderEntity.setItems(order.getItems().stream()
                .map(item -> {
                    OrderItemEntity itemEntity = new OrderItemEntity();
                    itemEntity.setId(item.getId());
                    itemEntity.setOrderId(item.getOrderId());
                    itemEntity.setProductId(item.getProductId());
                    itemEntity.setPrice(item.getPrice()); // Sử dụng float
                    itemEntity.setQuantity(item.getQuantity());
                    itemEntity.setSubTotal(item.getSubTotal()); // Sử dụng float
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
                orderEntity.getPrice(),
                OrderStatus.valueOf(orderEntity.getOrderStatus()), // Chuyển String sang OrderStatus
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
                orderEntity.getCreatedAt()
        );
    }
}