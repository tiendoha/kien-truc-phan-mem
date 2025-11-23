package com.system.order_application_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request to process payment for an order via Kafka")
public class OrderPaymentRequest {

    @NotNull(message = "Customer ID is required")
    @Schema(description = "Unique identifier of the customer", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID customerId;

    @NotNull(message = "Order ID is required")
    @Schema(description = "Unique identifier of the order", example = "123e4567-e89b-12d3-a456-426614174001")
    private UUID orderId;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    @Schema(description = "Final price to be paid (after voucher discounts)", example = "29.99")
    private BigDecimal price;
}