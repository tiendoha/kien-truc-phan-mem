package com.system.order_application_service.dto;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

public class OrderResponse {
    private UUID id;
    private UUID customerId;
    private UUID restaurantId;
    private UUID trackingId;
    private BigDecimal price;
    private String orderStatus;
    private List<OrderItemResponse> items;
    private String failureMessages;
    private ZonedDateTime createdAt;

    // Default constructor
    public OrderResponse() {
    }

    // All-args constructor
    public OrderResponse(UUID id, UUID customerId, UUID restaurantId, UUID trackingId, BigDecimal price,
                         String orderStatus, List<OrderItemResponse> items, String failureMessages,
                         ZonedDateTime createdAt) {
        this.id = id;
        this.customerId = customerId;
        this.restaurantId = restaurantId;
        this.trackingId = trackingId;
        this.price = price;
        this.orderStatus = orderStatus;
        this.items = items;
        this.failureMessages = failureMessages;
        this.createdAt = createdAt;
    }

    // Getters and setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getCustomerId() { return customerId; }
    public void setCustomerId(UUID customerId) { this.customerId = customerId; }
    public UUID getRestaurantId() { return restaurantId; }
    public void setRestaurantId(UUID restaurantId) { this.restaurantId = restaurantId; }
    public UUID getTrackingId() { return trackingId; }
    public void setTrackingId(UUID trackingId) { this.trackingId = trackingId; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public String getOrderStatus() { return orderStatus; }
    public void setOrderStatus(String orderStatus) { this.orderStatus = orderStatus; }
    public List<OrderItemResponse> getItems() { return items; }
    public void setItems(List<OrderItemResponse> items) { this.items = items; }
    public String getFailureMessages() { return failureMessages; }
    public void setFailureMessages(String failureMessages) { this.failureMessages = failureMessages; }
    public ZonedDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(ZonedDateTime createdAt) { this.createdAt = createdAt; }
}