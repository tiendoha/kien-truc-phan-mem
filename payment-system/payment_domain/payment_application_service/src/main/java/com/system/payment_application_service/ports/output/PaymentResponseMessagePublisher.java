package com.system.payment_application_service.ports.output;

import com.system.payment_application_service.dto.PaymentResponseEvent;

public interface PaymentResponseMessagePublisher {
    void publish(PaymentResponseEvent event);
}