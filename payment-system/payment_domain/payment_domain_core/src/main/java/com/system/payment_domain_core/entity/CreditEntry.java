package com.system.payment_domain_core.entity;

import com.system.payment_domain_core.exception.PaymentDomainException;
import com.system.payment_domain_core.valueobject.CustomerId;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
public class CreditEntry {
    private final UUID id;
    private final CustomerId customerId;
    private BigDecimal totalCredit;

    public CreditEntry(UUID id, CustomerId customerId, BigDecimal totalCredit) {
        this.id = id;
        this.customerId = customerId;
        this.totalCredit = totalCredit;
    }

    public void subtractCredit(BigDecimal amount) {
        if (totalCredit.compareTo(amount) < 0) {
            throw new PaymentDomainException("Insufficient credit for customer: " + customerId.getValue());
        }
        this.totalCredit = this.totalCredit.subtract(amount);
    }
}