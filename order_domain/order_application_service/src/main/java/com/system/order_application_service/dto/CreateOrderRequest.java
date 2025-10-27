package com.system.order_application_service.dto;

import java.util.List;
import java.util.UUID;
public class CreateOrderRequest {
    private UUID customerId;
    private UUID restaurantId;
    private List<OrderItemRequest> items;

    // Getters and setters
    public UUID getCustomerId() { return customerId; }
    public void setCustomerId(UUID customerId) { this.customerId = customerId; }
    public UUID getRestaurantId() { return restaurantId; }
    public void setRestaurantId(UUID restaurantId) { this.restaurantId = restaurantId; }
    public List<OrderItemRequest> getItems() { return items; }
    public void setItems(List<OrderItemRequest> items) { this.items = items; }
}