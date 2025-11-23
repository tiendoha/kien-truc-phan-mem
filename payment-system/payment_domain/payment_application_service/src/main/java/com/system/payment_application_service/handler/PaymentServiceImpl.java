package com.system.payment_application_service.handler;

import com.system.payment_application_service.ports.input.PaymentService;
import com.system.payment_application_service.ports.output.CreditEntryRepository;
import com.system.payment_application_service.ports.output.PaymentRepository;
import com.system.payment_application_service.dto.*;
import com.system.payment_domain_core.entity.CreditEntry;
import com.system.payment_domain_core.entity.Payment;
import com.system.payment_domain_core.exception.PaymentDomainException;
import com.system.payment_domain_core.valueobject.CustomerId;
import com.system.payment_domain_core.valueobject.PaymentStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final CreditEntryRepository creditEntryRepository;

    @Override
    @Transactional
    public PaymentProcessResponse processPayment(PaymentProcessRequest paymentProcessRequest) {
        log.info("Processing payment for order: {} and customer: {} with amount: {}",
                paymentProcessRequest.getOrderId(), paymentProcessRequest.getCustomerId(), paymentProcessRequest.getPrice());

        CustomerId customerId = new CustomerId(paymentProcessRequest.getCustomerId());

        // Check if completed payment already exists for this order
        paymentRepository.findByOrderId(paymentProcessRequest.getOrderId())
                .ifPresent(existingPayment -> {
                    if (existingPayment.getPaymentStatus() == PaymentStatus.COMPLETED) {
                        throw new PaymentDomainException("Payment already completed for order: " + paymentProcessRequest.getOrderId());
                    }
                });

        // Get customer credit entry
        CreditEntry creditEntry = creditEntryRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new PaymentDomainException("Credit entry not found for customer: " + paymentProcessRequest.getCustomerId()));

        // Create new payment
        Payment payment = Payment.createPayment(
                paymentProcessRequest.getOrderId(),
                customerId,
                paymentProcessRequest.getPrice()
        );

        try {
            // Check if customer has sufficient credit and process payment
            if (creditEntry.hasSufficientCredit(paymentProcessRequest.getPrice())) {
                    
                // Complete the payment
                payment.processPayment(creditEntry.getTotalCredit());
                    
                // Subtract credit from customer account
                creditEntry.subtractCredit(paymentProcessRequest.getPrice());
                // Save credit entry and payment
                creditEntryRepository.save(creditEntry);
                Payment savedPayment = paymentRepository.save(payment);

                log.info("Payment completed successfully. Payment ID: {}, Remaining credit: {}",
                        savedPayment.getId().getValue(), creditEntry.getTotalCredit());

                return PaymentProcessResponse.builder()
                        .paymentId(savedPayment.getId().getValue())
                        .orderId(savedPayment.getOrderId())
                        .customerId(savedPayment.getCustomerId().getValue())
                        .price(savedPayment.getPrice())
                        .status(PaymentStatus.COMPLETED)
                        .message("Payment processed successfully")
                        .build();

            } else {
                // Fail the payment due to insufficient credit
                payment.fail();
                paymentRepository.save(payment);

                log.warn("Payment failed due to insufficient credit. Order ID: {}, Available: {}, Required: {}",
                        paymentProcessRequest.getOrderId(), creditEntry.getTotalCredit(), paymentProcessRequest.getPrice());

                return PaymentProcessResponse.builder()
                        .paymentId(payment.getId().getValue())
                        .orderId(payment.getOrderId())
                        .customerId(payment.getCustomerId().getValue())
                        .price(payment.getPrice())
                        .status(PaymentStatus.FAILED)
                        .message(String.format("Insufficient credit. Available: %.2f, Required: %.2f",
                                creditEntry.getTotalCredit(), paymentProcessRequest.getPrice()))
                        .build();
            }

        } catch (PaymentDomainException e) {
            // Mark payment as failed if any domain rule violation occurs
            payment.fail();
            paymentRepository.save(payment);
            throw e;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentGroupByStatusResponse getPaymentsGroupByStatus(UUID customerId) {
        log.info("Getting payments grouped by status for customer: {}", customerId);

        List<Payment> payments = paymentRepository.findByCustomerIdOptional(customerId);

        if (payments.isEmpty()) {
            log.info("No payments found for customer: {}", customerId);
            return PaymentGroupByStatusResponse.builder()
                    .customerId(customerId)
                    .totalCount(0)
                    .totalAmount(BigDecimal.ZERO)
                    .statusGroups(List.of())
                    .build();
        }

        // Group payments by status
        Map<PaymentStatus, List<Payment>> paymentsByStatus = payments.stream()
                .collect(Collectors.groupingBy(Payment::getPaymentStatus));

        // Create status groups
        List<PaymentStatusGroupDto> statusGroups = paymentsByStatus.entrySet().stream()
                .map(entry -> {
                    PaymentStatus status = entry.getKey();
                    List<Payment> statusPayments = entry.getValue();

                    // Create payment summaries
                    List<PaymentSummaryDto> paymentSummaries = statusPayments.stream()
                            .map(payment -> PaymentSummaryDto.builder()
                                    .paymentId(payment.getId().getValue())
                                    .orderId(payment.getOrderId())
                                    .customerId(payment.getCustomerId().getValue())
                                    .price(payment.getPrice())
                                    .status(payment.getPaymentStatus())
                                    .createdAt(payment.getCreatedAt())
                                    .build())
                            .collect(Collectors.toList());

                    // Calculate total amount for this status
                    BigDecimal totalAmount = statusPayments.stream()
                            .map(Payment::getPrice)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    return PaymentStatusGroupDto.builder()
                            .status(status)
                            .count(statusPayments.size())
                            .totalAmount(totalAmount)
                            .payments(paymentSummaries)
                            .build();
                })
                .collect(Collectors.toList());

        // Calculate overall statistics
        int totalCount = payments.size();
        BigDecimal totalAmount = payments.stream()
                .map(Payment::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        log.info("Found {} payments for customer: {} with total amount: {}",
                totalCount, customerId, totalAmount);

        return PaymentGroupByStatusResponse.builder()
                .customerId(customerId)
                .totalCount(totalCount)
                .totalAmount(totalAmount)
                .statusGroups(statusGroups)
                .build();
    }
}