package com.system.order_dataaccess.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class OrderItemId implements Serializable {  // Phải Serializable cho @EmbeddedId

    @Column(name = "id", nullable = false)
    private Long id;  // Generated part

    @Column(name = "order_id", nullable = false)
    private UUID orderId;  // Assigned from parent

    // Constructors
    public OrderItemId() {}

    public OrderItemId(Long id, UUID orderId) {
        this.id = id;
        this.orderId = orderId;
    }

    // Equals and HashCode (bắt buộc cho @EmbeddedId)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderItemId that = (OrderItemId) o;
        return Objects.equals(id, that.id) && Objects.equals(orderId, that.orderId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, orderId);
    }
}