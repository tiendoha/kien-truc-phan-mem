package com.system.order_application_service.ports.input;

import com.system.order_application_service.dto.PaymentResponse;

// Input port để lắng nghe kết quả payment
public interface PaymentResponseMessageListener {
    void paymentCompleted(PaymentResponse paymentResponse);
    void paymentFailed(PaymentResponse paymentResponse);
}