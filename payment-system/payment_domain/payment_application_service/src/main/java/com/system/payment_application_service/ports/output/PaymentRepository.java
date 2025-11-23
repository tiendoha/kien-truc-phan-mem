package com.system.payment_application_service.ports.output;

import com.system.payment_domain_core.entity.Payment;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository {
    Payment save(Payment payment);
    Optional<Payment> findByOrderId(UUID orderId);
    List<Payment> findByCustomerId(UUID customerId);
    List<Payment> findAll();
    List<Payment> findByCustomerIdOptional(UUID customerId);
}