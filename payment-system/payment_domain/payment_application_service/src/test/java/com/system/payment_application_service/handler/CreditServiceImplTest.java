package com.system.payment_application_service.handler;

import com.system.payment_application_service.ports.output.CreditEntryRepository;
import com.system.payment_application_service.dto.UpdateCreditRequest;
import com.system.payment_domain_core.entity.CreditEntry;
import com.system.payment_domain_core.exception.PaymentDomainException;
import com.system.payment_domain_core.valueobject.CustomerId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Credit Service Implementation Tests")
class CreditServiceImplTest {

    @Mock
    private CreditEntryRepository creditEntryRepository;

    @InjectMocks
    private CreditServiceImpl creditService;

    private UUID customerId;
    private UUID creditId;
    private CustomerId customerIdValueObject;
    private CreditEntry mockCreditEntry;

    @BeforeEach
    void setUp() {
        customerId = UUID.randomUUID();
        creditId = UUID.randomUUID();
        customerIdValueObject = new CustomerId(customerId);
        mockCreditEntry = new CreditEntry(creditId, customerIdValueObject, new BigDecimal("1000.00"));
    }

    @Test
    @DisplayName("Get total credit by customer ID - Success")
    void whenGetTotalCreditByCustomerId_thenReturnCreditResponse() {
        when(creditEntryRepository.findByCustomerId(customerIdValueObject))
                .thenReturn(Optional.of(mockCreditEntry));

        var result = creditService.getTotalCreditByCustomerId(customerId);

        assertThat(result.getId()).isEqualTo(creditId);
        assertThat(result.getCustomerId()).isEqualTo(customerId);
        assertThat(result.getTotalCredit()).isEqualByComparingTo("1000.00");

        verify(creditEntryRepository).findByCustomerId(customerIdValueObject);
    }

    @Test
    @DisplayName("Get total credit by customer ID - Not Found")
    void whenGetTotalCreditByCustomerIdNotFound_thenThrowException() {
        when(creditEntryRepository.findByCustomerId(customerIdValueObject))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> creditService.getTotalCreditByCustomerId(customerId))
                .isInstanceOf(PaymentDomainException.class)
                .hasMessage("Credit entry not found for customer: " + customerId);

        verify(creditEntryRepository).findByCustomerId(customerIdValueObject);
    }

    @Test
    @DisplayName("Update total credit - Existing entry")
    void whenUpdateTotalCreditExistingEntry_thenReturnUpdatedCredit() {
        UpdateCreditRequest request = UpdateCreditRequest.builder()
                .customerId(customerId)
                .totalCredit(new BigDecimal("1500.00"))
                .build();

        CreditEntry updatedCreditEntry = new CreditEntry(creditId, customerIdValueObject, new BigDecimal("1500.00"));
        when(creditEntryRepository.findByCustomerId(customerIdValueObject))
                .thenReturn(Optional.of(mockCreditEntry));
        when(creditEntryRepository.save(any(CreditEntry.class)))
                .thenReturn(updatedCreditEntry);

        var result = creditService.updateTotalCredit(request);

        assertThat(result.getId()).isEqualTo(creditId);
        assertThat(result.getCustomerId()).isEqualTo(customerId);
        assertThat(result.getTotalCredit()).isEqualByComparingTo("1500.00");

        verify(creditEntryRepository).findByCustomerId(customerIdValueObject);
        verify(creditEntryRepository).save(any(CreditEntry.class));
    }

    @Test
    @DisplayName("Update total credit - New entry created")
    void whenUpdateTotalCreditNewEntry_thenReturnNewCredit() {
        UpdateCreditRequest request = UpdateCreditRequest.builder()
                .customerId(customerId)
                .totalCredit(new BigDecimal("500.00"))
                .build();

        when(creditEntryRepository.findByCustomerId(customerIdValueObject))
                .thenReturn(Optional.empty());
        when(creditEntryRepository.save(any(CreditEntry.class)))
                .thenReturn(mockCreditEntry);

        var result = creditService.updateTotalCredit(request);

        assertThat(result.getId()).isEqualTo(creditId);
        assertThat(result.getCustomerId()).isEqualTo(customerId);
        assertThat(result.getTotalCredit()).isEqualByComparingTo("1000.00");

        verify(creditEntryRepository).findByCustomerId(customerIdValueObject);
        verify(creditEntryRepository).save(any(CreditEntry.class));
    }

    @Test
    @DisplayName("Add credit to customer - Existing entry")
    void whenAddCreditToCustomerExistingEntry_thenReturnUpdatedCredit() {
        BigDecimal addAmount = new BigDecimal("200.00");

        CreditEntry updatedCreditEntry = new CreditEntry(creditId, customerIdValueObject, new BigDecimal("1200.00"));
        when(creditEntryRepository.findByCustomerId(customerIdValueObject))
                .thenReturn(Optional.of(mockCreditEntry));
        when(creditEntryRepository.save(any(CreditEntry.class)))
                .thenReturn(updatedCreditEntry);

        var result = creditService.addCreditToCustomer(customerId, addAmount);

        assertThat(result.getId()).isEqualTo(creditId);
        assertThat(result.getCustomerId()).isEqualTo(customerId);
        assertThat(result.getTotalCredit()).isEqualByComparingTo("1200.00");

        verify(creditEntryRepository).findByCustomerId(customerIdValueObject);
        verify(creditEntryRepository).save(any(CreditEntry.class));
    }

    @Test
    @DisplayName("Add credit to customer - New entry created")
    void whenAddCreditToCustomerNewEntry_thenReturnNewCredit() {
        BigDecimal addAmount = new BigDecimal("200.00");

        CreditEntry newCreditEntry = new CreditEntry(creditId, customerIdValueObject, new BigDecimal("200.00"));
        when(creditEntryRepository.findByCustomerId(customerIdValueObject))
                .thenReturn(Optional.empty());
        when(creditEntryRepository.save(any(CreditEntry.class)))
                .thenReturn(newCreditEntry);

        var result = creditService.addCreditToCustomer(customerId, addAmount);

        assertThat(result.getId()).isEqualTo(creditId);
        assertThat(result.getCustomerId()).isEqualTo(customerId);
        assertThat(result.getTotalCredit()).isEqualByComparingTo("200.00");

        verify(creditEntryRepository).findByCustomerId(customerIdValueObject);
        verify(creditEntryRepository).save(any(CreditEntry.class));
    }

    @Test
    @DisplayName("Add credit to customer - Negative amount")
    void whenAddCreditToCustomerWithNegativeAmount_thenThrowException() {
        BigDecimal negativeAmount = new BigDecimal("-100.00");

        assertThatThrownBy(() -> creditService.addCreditToCustomer(customerId, negativeAmount))
                .isInstanceOf(PaymentDomainException.class)
                .hasMessage("Credit amount must be positive for customer: " + customerId);

        verifyNoInteractions(creditEntryRepository);
    }
}