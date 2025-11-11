package com.system.payment_application_service.dto;

import com.system.payment_domain_core.valueobject.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PaymentResponseEvent {
    private UUID paymentId;
    private UUID orderId;
    private UUID customerId;
    private BigDecimal price;
    private PaymentStatus paymentStatus;
    private String failureMessage;
}