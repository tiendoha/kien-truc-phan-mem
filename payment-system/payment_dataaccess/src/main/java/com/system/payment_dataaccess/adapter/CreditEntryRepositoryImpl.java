package com.system.payment_dataaccess.adapter;

import com.system.payment_application_service.ports.output.CreditEntryRepository;
import com.system.payment_dataaccess.entity.CreditEntryEntity;
import com.system.payment_dataaccess.mapper.PaymentDataMapper;
import com.system.payment_dataaccess.repository.CreditEntryJpaRepository;
import com.system.payment_domain_core.entity.CreditEntry;
import com.system.payment_domain_core.valueobject.CustomerId;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class CreditEntryRepositoryImpl implements CreditEntryRepository {

    private final CreditEntryJpaRepository jpaRepository;
    private final PaymentDataMapper mapper;

    public CreditEntryRepositoryImpl(CreditEntryJpaRepository jpaRepository, PaymentDataMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Optional<CreditEntry> findByCustomerId(CustomerId customerId) {
        return jpaRepository.findByCustomerId(customerId.getValue())
                .map(mapper::creditEntryEntityToCreditEntry);
    }

    @Override
    public CreditEntry save(CreditEntry creditEntry) {
        CreditEntryEntity entity = mapper.creditEntryToCreditEntryEntity(creditEntry);
        CreditEntryEntity savedEntity = jpaRepository.save(entity);
        return mapper.creditEntryEntityToCreditEntry(savedEntity);
    }
}