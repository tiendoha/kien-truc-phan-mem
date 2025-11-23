package com.system.payment_domain_core.entity;

import com.system.payment_domain_core.exception.PaymentDomainException;
import com.system.payment_domain_core.valueobject.CustomerId;
import com.system.payment_domain_core.valueobject.PaymentId;
import com.system.payment_domain_core.valueobject.PaymentStatus;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

@Getter
public class Payment {
    private final PaymentId id;
    private final UUID orderId;
    private final CustomerId customerId;
    private final BigDecimal price;
    private PaymentStatus paymentStatus;
    private final ZonedDateTime createdAt;

    // Static factory for creation
    public static Payment createPayment(UUID orderId, CustomerId customerId, BigDecimal price) {
        return new Payment(new PaymentId(UUID.randomUUID()), orderId, customerId, price, PaymentStatus.PENDING, ZonedDateTime.now());
    }

    // Constructor
    public Payment(PaymentId id, UUID orderId, CustomerId customerId, BigDecimal price, PaymentStatus paymentStatus, ZonedDateTime createdAt) {
        this.id = id;
        this.orderId = orderId;
        this.customerId = customerId;
        this.price = price;
        this.paymentStatus = paymentStatus;
        this.createdAt = createdAt;
    }

    // Business Logic
    public void complete() {
        if (this.paymentStatus != PaymentStatus.PENDING) {
            throw new PaymentDomainException("Payment is not in PENDING state");
        }
        this.paymentStatus = PaymentStatus.COMPLETED;
    }

    public void fail() {
        if (this.paymentStatus != PaymentStatus.PENDING) {
            throw new PaymentDomainException("Payment is not in PENDING state");
        }
        this.paymentStatus = PaymentStatus.FAILED;
    }

    public void processPayment(BigDecimal availableCredit) {
        if (this.paymentStatus != PaymentStatus.PENDING) {
            throw new PaymentDomainException("Payment is not in PENDING state");
        }

        if (availableCredit.compareTo(this.price) < 0) {
            throw new PaymentDomainException(
                String.format("Insufficient credit. Available: %.2f, Required: %.2f",
                    availableCredit, this.price));
        }

        this.paymentStatus = PaymentStatus.COMPLETED;
    }
}