package com.system.payment_application_service.ports.output;

import com.system.payment_domain_core.entity.Payment;

public interface PaymentRepository {
    Payment save(Payment payment);
}