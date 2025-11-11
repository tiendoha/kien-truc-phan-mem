package com.system.payment_application_service.ports.input;

import com.system.payment_application_service.dto.PaymentRequest;

public interface PaymentRequestMessageListener {
    void processPayment(PaymentRequest paymentRequest);
}