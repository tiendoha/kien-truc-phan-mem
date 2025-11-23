package com.system.payment_container.rest.dto;

import com.system.payment_application_service.dto.PaymentProcessResponse;
import com.system.payment_domain_core.valueobject.PaymentStatus;
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
public class RestPaymentProcessResponse {

    private UUID paymentId;
    private UUID orderId;
    private UUID customerId;
    private BigDecimal price;
    private PaymentStatus status;
    private String message;

    public static RestPaymentProcessResponse fromPaymentProcessResponse(PaymentProcessResponse response) {
        return RestPaymentProcessResponse.builder()
                .paymentId(response.getPaymentId())
                .orderId(response.getOrderId())
                .customerId(response.getCustomerId())
                .price(response.getPrice())
                .status(response.getStatus())
                .message(response.getMessage())
                .build();
    }
}