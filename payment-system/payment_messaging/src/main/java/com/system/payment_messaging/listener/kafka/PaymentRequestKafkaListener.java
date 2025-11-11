package com.system.payment_messaging.listener.kafka;

import com.system.payment_application_service.dto.PaymentRequest;
import com.system.payment_application_service.ports.input.PaymentRequestMessageListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PaymentRequestKafkaListener {

    private final PaymentRequestMessageListener paymentMessageListener;

    public PaymentRequestKafkaListener(PaymentRequestMessageListener paymentMessageListener) {
        this.paymentMessageListener = paymentMessageListener;
    }

    // Lắng nghe topic mà OrderService gửi
    @KafkaListener(topics = "order.payment.request", groupId = "payment-consumer-group")
    public void receive(PaymentRequest paymentRequest) {
        paymentMessageListener.processPayment(paymentRequest);
    }
}