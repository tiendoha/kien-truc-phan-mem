package com.system.order_application_service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

// DTO cho message nhận từ payment service
@Getter
@Setter
@NoArgsConstructor
public class PaymentResponse {
    private UUID paymentId;
    private UUID orderId;
    private UUID customerId;
    private BigDecimal price;
    private String paymentStatus; // "COMPLETED" hoặc "FAILED"
    private String failureMessage;
}