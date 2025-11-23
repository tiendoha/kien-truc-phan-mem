package com.system.payment_container.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.system.payment_application_service.dto.UpdateCreditRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Builder
@Schema(description = "Request to update customer's total credit")
@NoArgsConstructor
public class RestUpdateCreditRequest {

    @NotNull(message = "Customer ID is required")
    @Schema(description = "Customer unique identifier",
            example = "550e8400-e29b-41d4-a716-446655440000",
            required = true)
    @JsonProperty("customerId")
    private UUID customerId;

    @NotNull(message = "Total credit amount is required")
    @DecimalMin(value = "0.0", message = "Total credit cannot be negative")
    @Schema(description = "New total credit amount",
            example = "1500.00",
            minimum = "0.0",
            required = true)
    @JsonProperty("totalCredit")
    private BigDecimal totalCredit;

    @JsonCreator
    public RestUpdateCreditRequest(@JsonProperty("customerId") UUID customerId,
                                   @JsonProperty("totalCredit") BigDecimal totalCredit) {
        this.customerId = customerId;
        this.totalCredit = totalCredit;
    }

    public UpdateCreditRequest toUpdateCreditRequest() {
        return UpdateCreditRequest.builder()
                .customerId(this.customerId)
                .totalCredit(this.totalCredit)
                .build();
    }
}