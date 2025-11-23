package com.system.order_messaging.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * DTO for receiving payment response events from payment service via Kafka
 * This is a simplified version that matches the expected payment service response format
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResponseEvent {
    private UUID paymentId;
    private UUID orderId;
    private UUID customerId;
    private BigDecimal price;
    private PaymentStatus paymentStatus;
    private String failureMessage;
    private String transactionId;

    public enum PaymentStatus {
        PENDING,
        PROCESSING,
        COMPLETED,
        FAILED
    }
}