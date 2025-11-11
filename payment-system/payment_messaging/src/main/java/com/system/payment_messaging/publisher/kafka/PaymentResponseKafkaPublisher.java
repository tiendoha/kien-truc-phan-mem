package com.system.payment_messaging.publisher.kafka;

import com.system.payment_application_service.dto.PaymentResponseEvent;
import com.system.payment_application_service.ports.output.PaymentResponseMessagePublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PaymentResponseKafkaPublisher implements PaymentResponseMessagePublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public PaymentResponseKafkaPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void publish(PaymentResponseEvent event) {
        String topic = "payment.order.response";
        try {
            kafkaTemplate.send(topic, event.getOrderId().toString(), event);
        } catch (Exception e) {
        }
    }
}