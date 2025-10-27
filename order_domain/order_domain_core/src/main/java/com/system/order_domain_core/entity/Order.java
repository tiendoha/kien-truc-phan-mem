package com.system.order_domain_core.entity;


import com.system.order_domain_core.valueobject.*;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

public class Order {
    private final OrderId id;
    private final CustomerId customerId;
    private final RestaurantId restaurantId;
    private final TrackingId trackingId;
    private final BigDecimal price;
    private OrderStatus orderStatus;
    private final List<OrderItem> items;
    private String failureMessages;
    private final ZonedDateTime createdAt;

    public Order(OrderId id, CustomerId customerId, RestaurantId restaurantId, TrackingId trackingId,
                 BigDecimal price, OrderStatus orderStatus, List<OrderItem> items, String failureMessages,
                 ZonedDateTime createdAt) {
        this.id = id;
        this.customerId = customerId;
        this.restaurantId = restaurantId;
        this.trackingId = trackingId;
        this.price = price;
        this.orderStatus = orderStatus;
        this.items = List.copyOf(items);
        this.failureMessages = failureMessages;
        this.createdAt = createdAt;
    }

    // Getters (và setters nếu cần)
    public OrderId getId() { return id; }
    public CustomerId getCustomerId() { return customerId; }
    public RestaurantId getRestaurantId() { return restaurantId; }
    public TrackingId getTrackingId() { return trackingId; }
    public BigDecimal getPrice() { return price; }
    public OrderStatus getOrderStatus() { return orderStatus; }
    public List<OrderItem> getItems() { return items; }
    public String getFailureMessages() { return failureMessages; }
    public ZonedDateTime getCreatedAt() { return createdAt; }
}