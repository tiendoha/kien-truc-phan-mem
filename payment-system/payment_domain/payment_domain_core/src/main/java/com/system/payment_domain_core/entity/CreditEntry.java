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

    public void addCredit(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new PaymentDomainException("Credit amount cannot be negative for customer: " + customerId.getValue());
        }
        this.totalCredit = this.totalCredit.add(amount);
    }

    public void updateTotalCredit(BigDecimal newTotalCredit) {
        if (newTotalCredit.compareTo(BigDecimal.ZERO) < 0) {
            throw new PaymentDomainException("Total credit cannot be negative for customer: " + customerId.getValue());
        }
        this.totalCredit = newTotalCredit;
    }

    public static CreditEntry createNewCreditEntry(UUID id, CustomerId customerId, BigDecimal initialCredit) {
        if (initialCredit.compareTo(BigDecimal.ZERO) < 0) {
            throw new PaymentDomainException("Initial credit cannot be negative for customer: " + customerId.getValue());
        }
        return new CreditEntry(id, customerId, initialCredit);
    }

    public boolean hasSufficientCredit(BigDecimal requiredAmount) {
        if (requiredAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new PaymentDomainException("Required amount cannot be negative for customer: " + customerId.getValue());
        }
        return this.totalCredit.compareTo(requiredAmount) >= 0;
    }
}