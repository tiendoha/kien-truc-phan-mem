package com.system.payment_application_service.handler;

import com.system.payment_application_service.dto.PaymentRequest;
import com.system.payment_application_service.dto.PaymentResponseEvent;
import com.system.payment_application_service.ports.input.PaymentRequestMessageListener;
import com.system.payment_application_service.ports.output.CreditEntryRepository;
import com.system.payment_application_service.ports.output.PaymentRepository;
import com.system.payment_application_service.ports.output.PaymentResponseMessagePublisher;
import com.system.payment_domain_core.entity.CreditEntry;
import com.system.payment_domain_core.entity.Payment;
import com.system.payment_domain_core.valueobject.CustomerId;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;

@Slf4j
public class PaymentRequestMessageListenerImpl implements PaymentRequestMessageListener {

    private final PaymentRepository paymentRepository;
    private final CreditEntryRepository creditEntryRepository;
    private final PaymentResponseMessagePublisher paymentResponseMessagePublisher;

    public PaymentRequestMessageListenerImpl(PaymentRepository paymentRepository,
                                             CreditEntryRepository creditEntryRepository,
                                             PaymentResponseMessagePublisher paymentResponseMessagePublisher) {
        this.paymentRepository = Objects.requireNonNull(paymentRepository);
        this.creditEntryRepository = Objects.requireNonNull(creditEntryRepository);
        this.paymentResponseMessagePublisher = Objects.requireNonNull(paymentResponseMessagePublisher);
    }

    @Override
    @Transactional
    public void processPayment(PaymentRequest paymentRequest) {
        Payment payment = Payment.createPayment(
                paymentRequest.getOrderId(),
                new CustomerId(paymentRequest.getCustomerId()),
                paymentRequest.getPrice()
        );

        Optional<CreditEntry> creditEntryOpt = creditEntryRepository.findByCustomerId(payment.getCustomerId());
        if (creditEntryOpt.isEmpty()) {
            return; // Hoặc ném exception
        }

        CreditEntry creditEntry = creditEntryOpt.get();
        String failureMessage = null;

        try {
            if (creditEntry.getTotalCredit().compareTo(payment.getPrice()) >= 0) {
                creditEntry.subtractCredit(payment.getPrice());
                creditEntryRepository.save(creditEntry);
                payment.complete();
            } else {
                payment.fail();
                failureMessage = "Insufficient funds";
            }
        } catch (Exception e) {
            payment.fail();
            failureMessage = e.getMessage();
        }

        paymentRepository.save(payment);

        PaymentResponseEvent event = new PaymentResponseEvent(
                payment.getId().getValue(),
                payment.getOrderId(),
                payment.getCustomerId().getValue(),
                payment.getPrice(),
                payment.getPaymentStatus(),
                failureMessage
        );

        paymentResponseMessagePublisher.publish(event);
    }
}