package com.system.payment_container.rest;

import com.system.payment_application_service.ports.input.CreditService;
import com.system.payment_application_service.dto.CreditResponse;
import com.system.payment_container.rest.dto.RestCreditResponse;
import com.system.payment_container.rest.dto.RestUpdateCreditRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/credits")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Credit Management", description = "APIs for managing customer credits")
public class CreditController {

    private final CreditService creditService;

    @Operation(
            summary = "Get total credit by customer ID",
            description = "Retrieve the current total credit balance for a specific customer",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved credit information",
                            content = @Content(schema = @Schema(implementation = RestCreditResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Customer not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @GetMapping("/total")
    public ResponseEntity<RestCreditResponse> getTotalCreditByCustomerId(
            @Parameter(description = "Customer unique identifier", required = true,
                    example = "550e8400-e29b-41d4-a716-446655440000")
            @RequestParam("customerId") UUID customerId) {
        log.info("REST request to get total credit for customer: {}", customerId);

        CreditResponse response = creditService.getTotalCreditByCustomerId(customerId);
        return ResponseEntity.ok(RestCreditResponse.fromCreditResponse(response));
    }

    @Operation(
            summary = "Update total credit for a customer",
            description = "Set the total credit balance for a specific customer. Creates a new credit entry if it doesn't exist.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully updated credit information",
                            content = @Content(schema = @Schema(implementation = RestCreditResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid input data"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @PutMapping("/total")
    public ResponseEntity<RestCreditResponse> updateTotalCredit(
            @Parameter(description = "Credit update request", required = true)
            @Valid @RequestBody RestUpdateCreditRequest updateCreditRequest) {
        log.info("REST request to update total credit for customer: {} with amount: {}",
                updateCreditRequest.getCustomerId(), updateCreditRequest.getTotalCredit());

        CreditResponse response = creditService.updateTotalCredit(updateCreditRequest.toUpdateCreditRequest());
        return ResponseEntity.ok(RestCreditResponse.fromCreditResponse(response));
    }

    @Operation(
            summary = "Add credit to customer account",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Successfully added credit",
                            content = @Content(schema = @Schema(implementation = RestCreditResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid amount (must be positive)"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @PostMapping("/add")
    public ResponseEntity<RestCreditResponse> addCreditToCustomer(
            @Parameter(description = "Customer unique identifier", required = true,
                    example = "550e8400-e29b-41d4-a716-446655440000")
            @RequestParam("customerId") UUID customerId,
            @Parameter(description = "Amount to add to credit balance", required = true,
                    example = "200.00", schema = @Schema(minimum = "0.01"))
            @RequestParam("amount") BigDecimal amount) {
        log.info("REST request to add credit {} for customer: {}", amount, customerId);

        CreditResponse response = creditService.addCreditToCustomer(customerId, amount);
        return ResponseEntity.status(HttpStatus.CREATED).body(RestCreditResponse.fromCreditResponse(response));
    }
}