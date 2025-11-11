package com.system.order_domain_core.valueobject;

import java.util.Objects;
import java.util.UUID;

public class RestaurantId {
    private final UUID value;

    public RestaurantId(UUID value) {
        this.value = Objects.requireNonNull(value, "Restaurant ID cannot be null");
    }

    public UUID getValue() { return value; }
}