package com.system.payment_application_service.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Builder
public class UpdateCreditRequest {

    private final UUID customerId;
    private final BigDecimal totalCredit;
}