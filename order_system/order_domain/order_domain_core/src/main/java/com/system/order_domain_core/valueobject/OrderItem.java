package com.system.order_domain_core.valueobject;

import java.math.BigDecimal;
import java.util.UUID;
public class OrderItem {
    private final Long id;
    private final UUID orderId;
    private final UUID productId;
    private final BigDecimal price;
    private final Integer quantity;
    private final BigDecimal subTotal;

    public OrderItem(Long id, UUID orderId, UUID productId, BigDecimal price, Integer quantity, BigDecimal subTotal) {
        this.id = id;
        this.orderId = orderId;
        this.productId = productId;
        this.price = price;
        this.quantity = quantity;
        this.subTotal = subTotal;
    }

    public Long getId() { return id; }
    public UUID getOrderId() { return orderId; }
    public UUID getProductId() { return productId; }
    public BigDecimal getPrice() { return price; }
    public Integer getQuantity() { return quantity; }
    public BigDecimal getSubTotal() { return subTotal; }
}