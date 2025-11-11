package com.system.payment_container.config;

import com.system.payment_application_service.handler.PaymentRequestMessageListenerImpl;
import com.system.payment_application_service.ports.input.PaymentRequestMessageListener;
import com.system.payment_application_service.ports.output.CreditEntryRepository;
import com.system.payment_application_service.ports.output.PaymentRepository;
import com.system.payment_application_service.ports.output.PaymentResponseMessagePublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PaymentConfig {

    @Bean
    public PaymentRequestMessageListener paymentRequestMessageListener(
            PaymentRepository paymentRepository,
            CreditEntryRepository creditEntryRepository,
            PaymentResponseMessagePublisher paymentResponseMessagePublisher) {

        return new PaymentRequestMessageListenerImpl(
                paymentRepository,
                creditEntryRepository,
                paymentResponseMessagePublisher
        );
    }

    // Các beans cho dataaccess và messaging adapters sẽ được
    // Spring tự động quét qua @SpringBootApplication(scanBasePackages = "com.system")
    // và @EnableJpaRepositories
}