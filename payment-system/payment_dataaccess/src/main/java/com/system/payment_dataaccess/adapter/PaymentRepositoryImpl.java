package com.system.payment_dataaccess.adapter;

import com.system.payment_application_service.ports.output.PaymentRepository;
import com.system.payment_dataaccess.entity.PaymentEntity;
import com.system.payment_dataaccess.mapper.PaymentDataMapper;
import com.system.payment_dataaccess.repository.PaymentJpaRepository;
import com.system.payment_domain_core.entity.Payment;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

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

    @Override
    public Optional<Payment> findByOrderId(UUID orderId) {
        Optional<PaymentEntity> entity = jpaRepository.findByOrderId(orderId);
        return entity.map(mapper::paymentEntityToPayment);
    }

    @Override
    public List<Payment> findByCustomerId(UUID customerId) {
        List<PaymentEntity> entities = jpaRepository.findByCustomerId(customerId);
        return entities.stream()
                .map(mapper::paymentEntityToPayment)
                .collect(Collectors.toList());
    }

    @Override
    public List<Payment> findAll() {
        List<PaymentEntity> entities = jpaRepository.findAll();
        return entities.stream()
                .map(mapper::paymentEntityToPayment)
                .collect(Collectors.toList());
    }

    @Override
    public List<Payment> findByCustomerIdOptional(UUID customerId) {
        List<PaymentEntity> entities = jpaRepository.findByCustomerIdOptional(customerId);
        return entities.stream()
                .map(mapper::paymentEntityToPayment)
                .collect(Collectors.toList());
    }
}