package com.system.order_domain_core.valueobject;

import java.util.Objects;
import java.util.UUID;

public class OrderId {
    private final UUID value;

    public OrderId(UUID value) {
        this.value = Objects.requireNonNull(value, "Order ID cannot be null");
    }

    public UUID getValue() { return value; }
}