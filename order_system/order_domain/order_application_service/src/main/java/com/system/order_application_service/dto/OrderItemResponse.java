package com.system.order_application_service.dto;

import java.math.BigDecimal;
import java.util.UUID;

public class OrderItemResponse {
    private Long id;
    private UUID orderId;
    private UUID productId;
    private BigDecimal price;
    private Integer quantity;
    private BigDecimal subTotal;

    // Default constructor
    public OrderItemResponse() {
    }

    // All-args constructor
    public OrderItemResponse(Long id, UUID orderId, UUID productId, BigDecimal price, Integer quantity, BigDecimal subTotal) {
        this.id = id;
        this.orderId = orderId;
        this.productId = productId;
        this.price = price;
        this.quantity = quantity;
        this.subTotal = subTotal;
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public UUID getOrderId() { return orderId; }
    public void setOrderId(UUID orderId) { this.orderId = orderId; }
    public UUID getProductId() { return productId; }
    public void setProductId(UUID productId) { this.productId = productId; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public BigDecimal getSubTotal() { return subTotal; }
    public void setSubTotal(BigDecimal subTotal) { this.subTotal = subTotal; }
}