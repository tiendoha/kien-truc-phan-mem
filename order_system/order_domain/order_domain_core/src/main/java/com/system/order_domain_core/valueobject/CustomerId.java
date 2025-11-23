package com.system.order_domain_core.valueobject;

import java.util.Objects;
import java.util.UUID;

public class CustomerId {
    private final UUID value;

    public CustomerId(UUID value) {
        this.value = Objects.requireNonNull(value, "Customer ID cannot be null");
    }

    public UUID getValue() { return value; }
}
