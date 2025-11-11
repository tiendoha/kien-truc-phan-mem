package com.system.order_application_service.ports.output;

import com.system.order_domain_core.entity.Order;

// Output port để publish sự kiện order created
public interface OrderCreatedPaymentRequestMessagePublisher {
    void publish(Order order);
}