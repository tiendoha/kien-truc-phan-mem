package com.system.order_messaging.listener.kafka;

import com.system.order_application_service.dto.PaymentStatusUpdate;
import com.system.order_application_service.service.PaymentStatusSseService;
import com.system.order_messaging.dto.PaymentResponseEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
public class PaymentResponseListener {

    private final PaymentStatusSseService paymentStatusSseService;

    public PaymentResponseListener(PaymentStatusSseService paymentStatusSseService) {
        this.paymentStatusSseService = paymentStatusSseService;
    }

    @KafkaListener(topics = "payment.order.response", groupId = "order-payment-response-consumer-group")
    public void handlePaymentResponse(PaymentResponseEvent paymentResponse) {
        log.info("Received payment response for order: {} with status: {}",
                paymentResponse.getOrderId(), paymentResponse.getPaymentStatus());

        // Convert payment response to status update
        PaymentStatusUpdate statusUpdate = PaymentStatusUpdate.builder()
                .orderId(paymentResponse.getOrderId())
                .customerId(paymentResponse.getCustomerId())
                .status(paymentResponse.getPaymentStatus().toString())
                .message(getStatusMessage(paymentResponse.getPaymentStatus(), paymentResponse.getFailureMessage()))
                .timestamp(LocalDateTime.now())
                .amount(paymentResponse.getPrice())
                .transactionId(paymentResponse.getPaymentId() != null ? paymentResponse.getPaymentId().toString() : null)
                .build();

        // Send status update via SSE
        paymentStatusSseService.sendStatusUpdate(
                paymentResponse.getOrderId().toString(),
                statusUpdate
        );
    }

    private String getStatusMessage(PaymentResponseEvent.PaymentStatus status, String failureMessage) {
        switch (status) {
            case COMPLETED:
                return "Payment processed successfully";
            case FAILED:
                return failureMessage != null ? failureMessage : "Payment processing failed";
            case PENDING:
                return "Payment is pending processing";
            default:
                return "Payment status updated to: " + status;
        }
    }
}