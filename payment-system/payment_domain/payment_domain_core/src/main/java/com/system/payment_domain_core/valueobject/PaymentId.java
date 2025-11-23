package com.system.payment_domain_core.valueobject;

import java.util.Objects;
import java.util.UUID;

public class PaymentId {
    private final UUID value;

    public PaymentId(UUID value) {
        this.value = Objects.requireNonNull(value, "Payment ID cannot be null");
    }

    public UUID getValue() { return value; }
}