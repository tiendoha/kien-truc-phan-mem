package com.system.payment_application_service.ports.output;

import com.system.payment_domain_core.entity.CreditEntry;
import com.system.payment_domain_core.valueobject.CustomerId;

import java.util.Optional;

public interface CreditEntryRepository {
    Optional<CreditEntry> findByCustomerId(CustomerId customerId);
    CreditEntry save(CreditEntry creditEntry);
}