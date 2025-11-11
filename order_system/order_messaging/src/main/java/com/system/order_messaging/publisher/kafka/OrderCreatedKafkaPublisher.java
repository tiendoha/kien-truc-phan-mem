package com.system.order_messaging.publisher.kafka;

import com.system.order_application_service.ports.output.OrderCreatedPaymentRequestMessagePublisher;
import com.system.order_domain_core.entity.Order;
import com.system.order_messaging.dto.OrderPaymentRequest;
import com.system.order_messaging.mapper.OrderMessagingDataMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OrderCreatedKafkaPublisher implements OrderCreatedPaymentRequestMessagePublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final OrderMessagingDataMapper mapper;

    public OrderCreatedKafkaPublisher(KafkaTemplate<String, Object> kafkaTemplate, OrderMessagingDataMapper mapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.mapper = mapper;
    }

    @Override
    public void publish(Order order) {
        String topic = "order.payment.request";
        OrderPaymentRequest request = mapper.orderToOrderPaymentRequest(order);


        try {
            kafkaTemplate.send(topic, order.getId().getValue().toString(), request);
        } catch (Exception e) {
//            log.error("Error publishing order created event for order id: {}", order.getId().getValue(), e);
        }
    }
}