package com.system.order_application_service.ports.output;

import com.system.order_application_service.dto.OrderPaymentRequest;

// Output port để publish payment request từ order
public interface OrderPaymentMessagePublisher {
    void publish(OrderPaymentRequest request);
}