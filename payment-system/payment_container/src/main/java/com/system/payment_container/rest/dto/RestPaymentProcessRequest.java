package com.system.payment_container.rest.dto;

import com.system.payment_application_service.dto.PaymentProcessRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RestPaymentProcessRequest {

    @NotNull(message = "Order ID is required")
    private UUID orderId;

    @NotNull(message = "Customer ID is required")
    private UUID customerId;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    private BigDecimal price;

    public PaymentProcessRequest toPaymentProcessRequest() {
        return PaymentProcessRequest.builder()
                .orderId(this.orderId)
                .customerId(this.customerId)
                .price(this.price)
                .build();
    }
}