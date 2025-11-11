package com.system.payment_dataaccess.adapter;

import com.system.payment_application_service.ports.output.PaymentRepository;
import com.system.payment_dataaccess.entity.PaymentEntity;
import com.system.payment_dataaccess.mapper.PaymentDataMapper;
import com.system.payment_dataaccess.repository.PaymentJpaRepository;
import com.system.payment_domain_core.entity.Payment;
import org.springframework.stereotype.Repository;

@Repository
public class PaymentRepositoryImpl implements PaymentRepository {

    private final PaymentJpaRepository jpaRepository;
    private final PaymentDataMapper mapper;

    public PaymentRepositoryImpl(PaymentJpaRepository jpaRepository, PaymentDataMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Payment save(Payment payment) {
        PaymentEntity entity = mapper.paymentToPaymentEntity(payment);
        PaymentEntity savedEntity = jpaRepository.save(entity);
        return mapper.paymentEntityToPayment(savedEntity);
    }
}