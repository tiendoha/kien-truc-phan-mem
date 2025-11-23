package com.system.payment_application_service.dto;

import com.system.payment_domain_core.valueobject.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentSummaryDto {
    private UUID paymentId;
    private UUID orderId;
    private UUID customerId;
    private BigDecimal price;
    private PaymentStatus status;
    private ZonedDateTime createdAt;
}