package com.system.order_application_service.dto;

import java.math.BigDecimal;
import java.util.UUID;

public class OrderItemRequest {
    private UUID productId;
    private BigDecimal price;
    private Integer quantity;

    // Getters and setters
    public UUID getProductId() { return productId; }
    public void setProductId(UUID productId) { this.productId = productId; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
}