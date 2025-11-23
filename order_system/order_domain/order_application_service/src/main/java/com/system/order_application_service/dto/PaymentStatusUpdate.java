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
@Schema(description = "Payment status update for SSE streaming")
public class PaymentStatusUpdate {

    @Schema(description = "Unique identifier of the order", example = "123e4567-e89b-12d3-a456-426614174001")
    private UUID orderId;

    @Schema(description = "Unique identifier of the customer", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID customerId;

    @Schema(description = "Current payment status", example = "COMPLETED", allowableValues = {"PENDING", "PROCESSING", "COMPLETED", "FAILED"})
    private String status;

    @Schema(description = "Detailed message about the payment status", example = "Payment processed successfully")
    private String message;

    @Schema(description = "Timestamp of the status update", example = "2024-01-15T10:30:00")
    private LocalDateTime timestamp;

    @Schema(description = "Final amount charged", example = "29.99")
    private java.math.BigDecimal amount;

    @Schema(description = "Transaction ID from payment processor", example = "txn_123456789")
    private String transactionId;
}