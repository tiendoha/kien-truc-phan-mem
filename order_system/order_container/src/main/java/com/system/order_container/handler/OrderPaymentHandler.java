package com.system.order_container.handler;

import com.system.order_application_service.dto.OrderPaymentRequest;
import com.system.order_application_service.dto.OrderPaymentResponse;
import com.system.order_application_service.ports.output.OrderPaymentMessagePublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
public class OrderPaymentHandler {

    private final OrderPaymentMessagePublisher orderPaymentMessagePublisher;

    public OrderPaymentHandler(OrderPaymentMessagePublisher orderPaymentMessagePublisher) {
        this.orderPaymentMessagePublisher = orderPaymentMessagePublisher;
    }

    public OrderPaymentResponse processOrderPayment(OrderPaymentRequest request) {
        log.info("Processing order payment request for order: {} and customer: {} with amount: {}",
                request.getOrderId(), request.getCustomerId(), request.getPrice());

        // Publish to Kafka topic for async processing
        orderPaymentMessagePublisher.publish(request);

        // Build response
        return OrderPaymentResponse.builder()
                .orderId(request.getOrderId())
                .customerId(request.getCustomerId())
                .status("PENDING")
                .message("Payment request queued for processing")
                .timestamp(LocalDateTime.now())
                .statusUpdateUrl("/orders/payment/status/" + request.getOrderId())
                .build();
    }
}