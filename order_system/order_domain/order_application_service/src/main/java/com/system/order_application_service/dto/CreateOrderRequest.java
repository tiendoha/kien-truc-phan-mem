package com.system.order_application_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.UUID;
@Schema(description = "Request object for creating a new order")
public class CreateOrderRequest {
    @Schema(description = "Unique identifier of the customer placing the order", required = true, example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID customerId;

    @Schema(description = "Unique identifier of the restaurant", required = true, example = "987e6543-e21b-34d5-a678-542614174999")
    private UUID restaurantId;

    @Schema(description = "List of items to be included in the order", required = true)
    private List<OrderItemRequest> items;

    @Schema(description = "Optional voucher code for discount", example = "SAVE10")
    private String voucherCode;

    // Getters and setters
    public UUID getCustomerId() { return customerId; }
    public void setCustomerId(UUID customerId) { this.customerId = customerId; }
    public UUID getRestaurantId() { return restaurantId; }
    public void setRestaurantId(UUID restaurantId) { this.restaurantId = restaurantId; }
    public List<OrderItemRequest> getItems() { return items; }
    public void setItems(List<OrderItemRequest> items) { this.items = items; }

    public String getVoucherCode() { return voucherCode; }
    public void setVoucherCode(String voucherCode) { this.voucherCode = voucherCode; }
}