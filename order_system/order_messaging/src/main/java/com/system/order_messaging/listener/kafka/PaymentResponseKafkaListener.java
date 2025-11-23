package com.system.order_messaging.listener.kafka;

import com.system.order_application_service.dto.PaymentResponse;
import com.system.order_application_service.ports.input.PaymentResponseMessageListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PaymentResponseKafkaListener {

    private final PaymentResponseMessageListener paymentMessageListener;

    public PaymentResponseKafkaListener(PaymentResponseMessageListener paymentMessageListener) {
        this.paymentMessageListener = paymentMessageListener;
    }

    // Lắng nghe topic phản hồi từ PaymentService
    @KafkaListener(
            topics = "payment.order.response",
            groupId = "order-payment-response-consumer-group",
            properties = {
                    "spring.json.value.default.type=com.system.order_application_service.dto.PaymentResponse"
            }
    )
    public void receive(PaymentResponse paymentResponse) {

        if ("COMPLETED".equals(paymentResponse.getPaymentStatus())) {
            paymentMessageListener.paymentCompleted(paymentResponse);
        } else if ("FAILED".equals(paymentResponse.getPaymentStatus())) {
            paymentMessageListener.paymentFailed(paymentResponse);
        }
    }
}