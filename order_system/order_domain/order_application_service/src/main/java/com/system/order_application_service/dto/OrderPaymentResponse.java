package com.system.order_application_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Response for order payment processing request")
public class OrderPaymentResponse {

    @Schema(description = "Unique identifier of the order", example = "123e4567-e89b-12d3-a456-426614174001")
    private UUID orderId;

    @Schema(description = "Unique identifier of the customer", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID customerId;

    @Schema(description = "Current status of the payment request", example = "PENDING")
    private String status;

    @Schema(description = "Message describing the payment request result", example = "Payment request queued for processing")
    private String message;

    @Schema(description = "Timestamp when the request was created", example = "2024-01-15T10:30:00")
    private LocalDateTime timestamp;

    @Schema(description = "URL for SSE status updates", example = "http://localhost:8081/orders/payment/status/123e4567-e89b-12d3-a456-426614174001")
    private String statusUpdateUrl;
}