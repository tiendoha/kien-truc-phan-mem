package com.system.order_messaging.listener.kafka;

import com.system.order_application_service.dto.PaymentResponse;
import com.system.order_application_service.dto.PaymentStatusUpdate;
import com.system.order_application_service.ports.input.PaymentResponseMessageListener;
import com.system.order_application_service.service.PaymentStatusSseService;
import com.system.order_messaging.dto.OrderPaymentRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class OrderPaymentRequestListener {

    private final PaymentResponseMessageListener paymentResponseMessageListener;
    private final PaymentStatusSseService paymentStatusSseService;
    private final RestTemplate restTemplate;
    private final String paymentServiceUrl;
    private final Random random = new Random();

    public OrderPaymentRequestListener(PaymentResponseMessageListener paymentResponseMessageListener,
                                     PaymentStatusSseService paymentStatusSseService,
                                     RestTemplate restTemplate,
                                     @Value("${payment.service.url:http://localhost:8082}") String paymentServiceUrl) {
        this.paymentResponseMessageListener = paymentResponseMessageListener;
        this.paymentStatusSseService = paymentStatusSseService;
        this.restTemplate = restTemplate;
        this.paymentServiceUrl = paymentServiceUrl;
    }

    /**
     * Add a random delay between minMs and maxMs milliseconds
     * @param minMs minimum delay in milliseconds
     * @param maxMs maximum delay in milliseconds
     * @param operation description of the operation being delayed
     */
    private void addRandomDelay(long minMs, long maxMs, String operation) {
        try {
            long delayMs = minMs + random.nextInt((int) (maxMs - minMs));
            log.info("Adding {}ms delay for operation: {}", delayMs, operation);
            TimeUnit.MILLISECONDS.sleep(delayMs);
        } catch (InterruptedException e) {
            log.warn("Processing delay interrupted for operation: {}", operation);
            Thread.currentThread().interrupt();
        }
    }

    @KafkaListener(
        topics = "order.payment.request",
        groupId = "order-payment-request-consumer-group",
        properties = {
            "spring.json.value.default.type=com.system.order_messaging.dto.OrderPaymentRequest"
        }
    )
    public void handleOrderPaymentRequest(OrderPaymentRequest request) {
        log.info("Received order payment request for order: {} and customer: {} with amount: {}",
                request.getOrderId(), request.getCustomerId(), request.getPrice());

        // Send initial processing status via SSE
        sendPaymentStatusUpdate(request, "PROCESSING", "Payment is being processed");

        try {
            // Add delay before preparing request
            addRandomDelay(1000, 2000, "Payment request preparation for order " + request.getOrderId());

            // Prepare the request for Payment Service API
            PaymentProcessRequest paymentRequest = PaymentProcessRequest.builder()
                    .orderId(request.getOrderId())
                    .customerId(request.getCustomerId())
                    .price(request.getPrice())
                    .build();

            // Call Payment Service API
            String url = paymentServiceUrl + "/api/v1/payments/process";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<PaymentProcessRequest> entity = new HttpEntity<>(paymentRequest, headers);

            log.info("Calling payment service API at: {}", url);
            ResponseEntity<PaymentProcessResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    PaymentProcessResponse.class
            );

            if (response.getStatusCode() == HttpStatus.OK) {
                // Add delay before processing response
                addRandomDelay(1000, 2000, "Payment response processing for order " + request.getOrderId());

                PaymentProcessResponse paymentResponse = response.getBody();
                if (paymentResponse != null && "COMPLETED".equals(paymentResponse.getStatus().toString())) {
                    log.info("Payment completed successfully for order: {}", request.getOrderId());
                    handlePaymentSuccess(request);
                } else {
                    log.warn("Payment failed for order: {} with status: {}",
                            request.getOrderId(),
                            paymentResponse != null ? paymentResponse.getStatus() : "UNKNOWN");
                    handlePaymentFailure(request, "Payment processing failed");
                }
            } else if (response.getStatusCode() == HttpStatus.PAYMENT_REQUIRED) {
                addRandomDelay(1000, 2000, "Payment required status processing for order " + request.getOrderId());
                log.warn("Payment processing failed with status PAYMENT_REQUIRED for order: {}", request.getOrderId());
                handlePaymentFailure(request, "Insufficient credit or payment declined");
            } else {
                addRandomDelay(1000, 2000, "Unexpected response processing for order " + request.getOrderId());
                log.error("Unexpected response from payment service: {} for order: {}",
                        response.getStatusCode(), request.getOrderId());
                handlePaymentFailure(request, "Payment service error: " + response.getStatusCode());
            }

        } catch (Exception e) {
            log.error("Error processing payment request for order: {}", request.getOrderId(), e);
            handlePaymentFailure(request, "Payment processing error: " + e.getMessage());
        }
    }

    private void handlePaymentSuccess(OrderPaymentRequest request) {
        try {
            PaymentResponse paymentResponse = new PaymentResponse();
            paymentResponse.setOrderId(request.getOrderId());
            paymentResponse.setCustomerId(request.getCustomerId());
            paymentResponse.setPrice(request.getPrice());
            paymentResponse.setPaymentId(UUID.randomUUID()); // Generate a payment ID for tracking
            paymentResponse.setPaymentStatus("COMPLETED");

            paymentResponseMessageListener.paymentCompleted(paymentResponse);

            // Send SSE notification for payment success
            sendPaymentStatusUpdate(request, "COMPLETED", "Payment processed successfully");

            log.info("Order status updated to PAID for order: {}", request.getOrderId());
        } catch (Exception e) {
            log.error("Error updating order status after successful payment for order: {}",
                    request.getOrderId(), e);
        }
    }

    private void handlePaymentFailure(OrderPaymentRequest request, String failureMessage) {
        try {
            PaymentResponse paymentResponse = new PaymentResponse();
            paymentResponse.setOrderId(request.getOrderId());
            paymentResponse.setCustomerId(request.getCustomerId());
            paymentResponse.setPrice(request.getPrice());
            paymentResponse.setFailureMessage(failureMessage);
            paymentResponse.setPaymentStatus("FAILED");

            paymentResponseMessageListener.paymentFailed(paymentResponse);
            // Send SSE notification for payment failure
            sendPaymentStatusUpdate(request, "FAILED", failureMessage);

            log.info("Order status updated to CANCELLED for order: {} due to payment failure",
                    request.getOrderId());
        } catch (Exception e) {
            log.error("Error updating order status after payment failure for order: {}",
                    request.getOrderId(), e);
        }
    }

    private void sendPaymentStatusUpdate(OrderPaymentRequest request, String status, String message) {
        try {
            PaymentStatusUpdate statusUpdate = PaymentStatusUpdate.builder()
                    .orderId(request.getOrderId())
                    .customerId(request.getCustomerId())
                    .status(status)
                    .message(message)
                    .timestamp(LocalDateTime.now())
                    .amount(request.getPrice())
                    .build();

            paymentStatusSseService.sendStatusUpdate(request.getOrderId().toString(), statusUpdate);
            log.info("Sent SSE status update for order {}: {} - {}",
                    request.getOrderId(), status, message);
        } catch (Exception e) {
            log.error("Error sending SSE status update for order: {}", request.getOrderId(), e);
        }
    }

    // DTOs for Payment Service API communication
    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class PaymentProcessRequest {
        private UUID orderId;
        private UUID customerId;
        private BigDecimal price;
    }

    @lombok.Data
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class PaymentProcessResponse {
        private UUID paymentId;
        private UUID orderId;
        private UUID customerId;
        private BigDecimal price;
        private PaymentStatus status;
        private String message;
        private java.time.ZonedDateTime createdAt;

        public enum PaymentStatus {
            PENDING, COMPLETED, FAILED
        }
    }
}