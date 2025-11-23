package com.system.order_application_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.UUID;

@Schema(description = "Individual item within an order")
public class OrderItemRequest {
    @Schema(description = "Unique identifier of the product", required = true, example = "456e7890-e12b-34d5-a678-642614174888")
    private UUID productId;

    @Schema(description = "Price of the product", required = true, example = "15.99")
    private BigDecimal price;

    @Schema(description = "Quantity of the product", required = true, example = "2")
    private Integer quantity;

    // Getters and setters
    public UUID getProductId() { return productId; }
    public void setProductId(UUID productId) { this.productId = productId; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
}