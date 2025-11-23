package com.system.payment_application_service.dto;

import com.system.payment_domain_core.valueobject.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentStatusGroupDto {
    private PaymentStatus status;
    private int count;
    private BigDecimal totalAmount;
    private List<PaymentSummaryDto> payments;
}