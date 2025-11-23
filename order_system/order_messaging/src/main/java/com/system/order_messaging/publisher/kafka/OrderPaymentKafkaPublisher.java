package com.system.order_messaging.publisher.kafka;

import com.system.order_application_service.dto.OrderPaymentRequest;
import com.system.order_application_service.ports.output.OrderPaymentMessagePublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OrderPaymentKafkaPublisher implements OrderPaymentMessagePublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public OrderPaymentKafkaPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void publish(OrderPaymentRequest request) {
        String topic = "order.payment.request";

        log.info("Publishing order payment request to topic: {} for order: {} and customer: {}",
                topic, request.getOrderId(), request.getCustomerId());

        // Convert to messaging DTO for Kafka
        com.system.order_messaging.dto.OrderPaymentRequest kafkaRequest =
            com.system.order_messaging.dto.OrderPaymentRequest.builder()
                .orderId(request.getOrderId())
                .customerId(request.getCustomerId())
                .price(request.getPrice())
                .build();

        try {
            kafkaTemplate.send(topic, request.getOrderId().toString(), kafkaRequest);
            log.info("Successfully published order payment request for order: {}", request.getOrderId());
        } catch (Exception e) {
            log.error("Error publishing order payment request for order id: {}", request.getOrderId(), e);
            throw new RuntimeException("Failed to publish order payment request", e);
        }
    }
}