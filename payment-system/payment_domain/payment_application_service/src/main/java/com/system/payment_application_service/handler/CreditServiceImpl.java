package com.system.payment_application_service.handler;

import com.system.payment_application_service.ports.input.CreditService;
import com.system.payment_application_service.ports.output.CreditEntryRepository;
import com.system.payment_application_service.dto.CreditResponse;
import com.system.payment_application_service.dto.UpdateCreditRequest;
import com.system.payment_domain_core.entity.CreditEntry;
import com.system.payment_domain_core.exception.PaymentDomainException;
import com.system.payment_domain_core.valueobject.CustomerId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreditServiceImpl implements CreditService {

    private final CreditEntryRepository creditEntryRepository;

    @Override
    @Transactional(readOnly = true)
    public CreditResponse getTotalCreditByCustomerId(UUID customerId) {
        log.info("Retrieving total credit for customer: {}", customerId);

        CustomerId custId = new CustomerId(customerId);
        CreditEntry creditEntry = creditEntryRepository.findByCustomerId(custId)
                .orElseThrow(() -> new PaymentDomainException("Credit entry not found for customer: " + customerId));

        return CreditResponse.builder()
                .id(creditEntry.getId())
                .customerId(creditEntry.getCustomerId().getValue())
                .totalCredit(creditEntry.getTotalCredit())
                .build();
    }

    @Override
    @Transactional
    public CreditResponse updateTotalCredit(UpdateCreditRequest updateCreditRequest) {
        log.info("Updating total credit for customer: {} with amount: {}",
                updateCreditRequest.getCustomerId(), updateCreditRequest.getTotalCredit());

        CustomerId customerId = new CustomerId(updateCreditRequest.getCustomerId());
        CreditEntry creditEntry = creditEntryRepository.findByCustomerId(customerId)
                .orElseGet(() -> createNewCreditEntry(customerId, updateCreditRequest.getTotalCredit()));

        creditEntry.updateTotalCredit(updateCreditRequest.getTotalCredit());

        CreditEntry savedCreditEntry = creditEntryRepository.save(creditEntry);

        return CreditResponse.builder()
                .id(savedCreditEntry.getId())
                .customerId(savedCreditEntry.getCustomerId().getValue())
                .totalCredit(savedCreditEntry.getTotalCredit())
                .build();
    }

    @Override
    @Transactional
    public CreditResponse addCreditToCustomer(UUID customerId, BigDecimal amount) {
        log.info("Adding credit {} for customer: {}", amount, customerId);

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new PaymentDomainException("Credit amount must be positive for customer: " + customerId);
        }

        CustomerId custId = new CustomerId(customerId);
        CreditEntry creditEntry = creditEntryRepository.findByCustomerId(custId)
                .orElseGet(() -> createNewCreditEntry(custId, BigDecimal.ZERO));

        creditEntry.addCredit(amount);

        CreditEntry savedCreditEntry = creditEntryRepository.save(creditEntry);

        return CreditResponse.builder()
                .id(savedCreditEntry.getId())
                .customerId(savedCreditEntry.getCustomerId().getValue())
                .totalCredit(savedCreditEntry.getTotalCredit())
                .build();
    }

    private CreditEntry createNewCreditEntry(CustomerId customerId, BigDecimal initialCredit) {
        log.info("Creating new credit entry for customer: {} with initial credit: {}", customerId.getValue(), initialCredit);
        return CreditEntry.createNewCreditEntry(
                UUID.randomUUID(),
                customerId,
                initialCredit
        );
    }
}