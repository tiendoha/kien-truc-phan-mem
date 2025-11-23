package com.system.payment_container.rest;

import com.system.payment_application_service.ports.input.PaymentService;
import com.system.payment_application_service.dto.PaymentGroupByStatusResponse;
import com.system.payment_application_service.dto.PaymentProcessResponse;
import com.system.payment_container.rest.dto.RestPaymentGroupByStatusResponse;
import com.system.payment_container.rest.dto.RestPaymentProcessRequest;
import com.system.payment_container.rest.dto.RestPaymentProcessResponse;
import com.system.payment_domain_core.valueobject.PaymentStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Payment Processing", description = "APIs for processing payments")
public class PaymentController {

    private final PaymentService paymentService;

    @Operation(
            summary = "Process payment for an order",
            description = "Process payment by checking customer credit, calculating price, and updating payment status. " +
                    "The customer must have sufficient credit for the payment to be successful."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payment processed successfully",
                    content = @Content(schema = @Schema(implementation = RestPaymentProcessResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data or insufficient credit"),
            @ApiResponse(responseCode = "404", description = "Customer not found"),
            @ApiResponse(responseCode = "409", description = "Payment already exists for this order"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/process")
    public ResponseEntity<RestPaymentProcessResponse> processPayment(
            @Valid @RequestBody RestPaymentProcessRequest paymentProcessRequest) {

        log.info("REST request to process payment for order: {} and customer: {} with amount: {}",
                paymentProcessRequest.getOrderId(), paymentProcessRequest.getCustomerId(), paymentProcessRequest.getPrice());

        PaymentProcessResponse response = paymentService.processPayment(paymentProcessRequest.toPaymentProcessRequest());

        HttpStatus status = response.getStatus() == PaymentStatus.COMPLETED ? HttpStatus.OK : HttpStatus.PAYMENT_REQUIRED;

        return ResponseEntity.status(status)
                .body(RestPaymentProcessResponse.fromPaymentProcessResponse(response));
    }

    @Operation(
            summary = "Get all payments grouped by status",
            description = "Retrieve all payments grouped by their status (PENDING, COMPLETED, FAILED). " +
                    "Can be filtered by customer ID. If no customer ID is provided, returns payments for all customers."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payments retrieved successfully",
                    content = @Content(schema = @Schema(implementation = RestPaymentGroupByStatusResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid customer ID format"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/group-by-status")
    public ResponseEntity<RestPaymentGroupByStatusResponse> getPaymentsGroupByStatus(
            @Parameter(description = "Customer ID to filter payments (optional)", required = false)
            @RequestParam(required = false) UUID customerId) {

        log.info("REST request to get payments grouped by status for customer: {}", customerId);

        PaymentGroupByStatusResponse response = paymentService.getPaymentsGroupByStatus(customerId);

        return ResponseEntity.ok(RestPaymentGroupByStatusResponse.fromPaymentGroupByStatusResponse(response));
    }
}