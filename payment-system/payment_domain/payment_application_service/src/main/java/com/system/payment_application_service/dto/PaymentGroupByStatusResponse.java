package com.system.payment_application_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentGroupByStatusResponse {
    private UUID customerId;
    private int totalCount;
    private BigDecimal totalAmount;
    private List<PaymentStatusGroupDto> statusGroups;
}