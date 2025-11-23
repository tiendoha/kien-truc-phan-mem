package com.system.payment_domain_core.exception;

public class PaymentDomainException extends RuntimeException {
    public PaymentDomainException(String message) {
        super(message);
    }
}