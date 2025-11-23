package com.system.payment_container.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.system.payment_application_service.dto.CreditResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Builder
@Schema(description = "Customer credit information response")
public class RestCreditResponse {

    @Schema(description = "Credit entry unique identifier",
            example = "123e4567-e89b-12d3-a456-426614174000")
    @JsonProperty("id")
    private UUID id;

    @Schema(description = "Customer unique identifier",
            example = "550e8400-e29b-41d4-a716-446655440000")
    @JsonProperty("customerId")
    private UUID customerId;

    @Schema(description = "Total credit amount available for the customer",
            example = "1500.00",
            minimum = "0.0")
    @JsonProperty("totalCredit")
    private BigDecimal totalCredit;

    public static RestCreditResponse fromCreditResponse(CreditResponse creditResponse) {
        return RestCreditResponse.builder()
                .id(creditResponse.getId())
                .customerId(creditResponse.getCustomerId())
                .totalCredit(creditResponse.getTotalCredit())
                .build();
    }
}