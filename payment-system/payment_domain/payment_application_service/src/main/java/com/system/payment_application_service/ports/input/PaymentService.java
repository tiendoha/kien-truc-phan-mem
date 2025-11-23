package com.system.payment_application_service.ports.input;

import com.system.payment_application_service.dto.PaymentGroupByStatusResponse;
import com.system.payment_application_service.dto.PaymentProcessRequest;
import com.system.payment_application_service.dto.PaymentProcessResponse;

import java.util.UUID;

public interface PaymentService {
    PaymentProcessResponse processPayment(PaymentProcessRequest paymentProcessRequest);
    PaymentGroupByStatusResponse getPaymentsGroupByStatus(UUID customerId);
}