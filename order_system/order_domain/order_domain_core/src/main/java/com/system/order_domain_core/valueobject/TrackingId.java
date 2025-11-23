package com.system.order_domain_core.valueobject;

import java.util.Objects;
import java.util.UUID;

public class TrackingId {
    private final UUID value;

    public TrackingId(UUID value) {
        this.value = Objects.requireNonNull(value, "Tracking ID cannot be null");
    }

    public UUID getValue() { return value; }
}