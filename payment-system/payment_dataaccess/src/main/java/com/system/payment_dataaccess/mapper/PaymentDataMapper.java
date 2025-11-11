package com.system.payment_dataaccess.mapper;

import com.system.payment_dataaccess.entity.CreditEntryEntity;
import com.system.payment_dataaccess.entity.PaymentEntity;
import com.system.payment_domain_core.entity.CreditEntry;
import com.system.payment_domain_core.entity.Payment;
import com.system.payment_domain_core.valueobject.CustomerId;
import com.system.payment_domain_core.valueobject.PaymentId;
import org.springframework.stereotype.Component;

@Component
public class PaymentDataMapper {

    // Payment Mappers
    public PaymentEntity paymentToPaymentEntity(Payment payment) {
        PaymentEntity entity = new PaymentEntity();
        entity.setId(payment.getId().getValue());
        entity.setOrderId(payment.getOrderId());
        entity.setCustomerId(payment.getCustomerId().getValue());
        entity.setPrice(payment.getPrice());
        entity.setPaymentStatus(payment.getPaymentStatus());
        entity.setCreatedAt(payment.getCreatedAt());
        return entity;
    }

    public Payment paymentEntityToPayment(PaymentEntity entity) {
        return new Payment(
                new PaymentId(entity.getId()),
                entity.getOrderId(),
                new CustomerId(entity.getCustomerId()),
                entity.getPrice(),
                entity.getPaymentStatus(),
                entity.getCreatedAt()
        );
    }

    // CreditEntry Mappers
    public CreditEntryEntity creditEntryToCreditEntryEntity(CreditEntry creditEntry) {
        CreditEntryEntity entity = new CreditEntryEntity();
        entity.setId(creditEntry.getId());
        entity.setCustomerId(creditEntry.getCustomerId().getValue());
        entity.setTotalCredit(creditEntry.getTotalCredit());
        return entity;
    }

    public CreditEntry creditEntryEntityToCreditEntry(CreditEntryEntity entity) {
        return new CreditEntry(
                entity.getId(),
                new CustomerId(entity.getCustomerId()),
                entity.getTotalCredit()
        );
    }
}