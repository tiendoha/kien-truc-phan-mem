package com.system.payment_application_service.ports.input;

import com.system.payment_application_service.dto.UpdateCreditRequest;
import com.system.payment_application_service.dto.CreditResponse;

import java.util.UUID;

public interface CreditService {

    CreditResponse getTotalCreditByCustomerId(UUID customerId);

    CreditResponse updateTotalCredit(UpdateCreditRequest updateCreditRequest);

    CreditResponse addCreditToCustomer(UUID customerId, java.math.BigDecimal amount);
}