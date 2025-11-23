package com.system.payment_domain_core.valueobject;

import java.util.Objects;
import java.util.UUID;

public class CustomerId {
    private final UUID value;

    public CustomerId(UUID value) {
        this.value = Objects.requireNonNull(value, "Customer ID cannot be null");
    }

    public UUID getValue() { return value; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CustomerId that = (CustomerId) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}