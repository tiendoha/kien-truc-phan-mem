package com.system.order_container.rest;

import com.system.order_application_service.dto.CreateOrderRequest;
import com.system.order_application_service.dto.OrderPaymentRequest;
import com.system.order_application_service.dto.OrderPaymentResponse;
import com.system.order_application_service.dto.OrderRatingRequest;
import com.system.order_application_service.dto.OrderResponse;
import com.system.order_application_service.dto.OrderStatisticsResponse;
import com.system.order_application_service.dto.UpdateOrderRequest;
import com.system.order_application_service.handler.OrderCreateHandler;
import com.system.order_container.handler.OrderPaymentHandler;
import com.system.order_application_service.handler.OrderQueryHandler;
import com.system.order_application_service.handler.OrderRatingHandler;
import com.system.order_application_service.handler.OrderStatisticsHandler;
import com.system.order_application_service.handler.OrderUpdateHandler;
import com.system.order_application_service.service.PaymentStatusSseService;
import com.system.order_container.rest.dto.RestOrderPaymentRequest;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(value="/orders",produces="application/vnd.api.v1+json")
@Tag(name = "Order Management", description = "APIs for managing orders, including creation, updates, ratings, and statistics")
public class OrderController {

    private final OrderCreateHandler orderCreateHandler;
    private final OrderQueryHandler orderQueryHandler;
    private final OrderUpdateHandler orderUpdateHandler;
    private final OrderRatingHandler orderRatingHandler; // <-- (Feature 5)
    private final OrderStatisticsHandler orderStatisticsHandler; // <-- (Feature 3)
    private final OrderPaymentHandler orderPaymentHandler; // <-- (New Feature)
    private final PaymentStatusSseService paymentStatusSseService; // <-- (New Feature)

    public OrderController(OrderCreateHandler orderCreateHandler,
                           OrderQueryHandler orderQueryHandler,
                           OrderUpdateHandler orderUpdateHandler,
                           OrderRatingHandler orderRatingHandler,
                           OrderStatisticsHandler orderStatisticsHandler,
                           OrderPaymentHandler orderPaymentHandler,
                           PaymentStatusSseService paymentStatusSseService) {
        this.orderCreateHandler = orderCreateHandler;
        this.orderQueryHandler = orderQueryHandler;
        this.orderUpdateHandler = orderUpdateHandler;
        this.orderRatingHandler = orderRatingHandler;
        this.orderStatisticsHandler = orderStatisticsHandler;
        this.orderPaymentHandler = orderPaymentHandler;
        this.paymentStatusSseService = paymentStatusSseService;
    }

    @PostMapping
    @Operation(summary = "Create a new order", description = "Creates a new order with specified items, voucher details, and customer information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Order created successfully",
                    content = @Content(schema = @Schema(implementation = OrderResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<OrderResponse> createOrder(
            @Parameter(description = "Order creation request with items and voucher details", required = true)
            @RequestBody CreateOrderRequest request) {
        OrderResponse result =  orderCreateHandler.createOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @GetMapping
    @Operation(summary = "Get orders by customer ID", description = "Retrieves all orders associated with a specific customer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Orders retrieved successfully",
                    content = @Content(schema = @Schema(implementation = OrderResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid customer ID format"),
            @ApiResponse(responseCode = "404", description = "Customer not found or no orders")
    })
    public ResponseEntity<List<OrderResponse>> getOrdersByCustomerId(
            @Parameter(description = "Unique identifier of the customer", required = true)
            @RequestParam(name = "customerId") UUID customerId) {
        List<OrderResponse> result = orderQueryHandler.getOrdersByCustomerId(customerId);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/{orderId}")
    @Operation(summary = "Update an existing order", description = "Updates items in an existing order. Only orders in PENDING status can be updated")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order updated successfully",
                    content = @Content(schema = @Schema(implementation = OrderResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request or order not in PENDING status"),
            @ApiResponse(responseCode = "404", description = "Order not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<OrderResponse> updateOrder(
            @Parameter(description = "Unique identifier of the order to update", required = true)
            @PathVariable UUID orderId,
            @Parameter(description = "Updated order items and voucher details", required = true)
            @RequestBody UpdateOrderRequest request) {
        OrderResponse result = orderUpdateHandler.updateOrder(orderId, request);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/{orderId}/rating")
    @Operation(summary = "Rate an order", description = "Adds a rating and optional comment to a completed order")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rating added successfully",
                    content = @Content(schema = @Schema(implementation = OrderResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid rating or order not eligible for rating"),
            @ApiResponse(responseCode = "404", description = "Order not found"),
            @ApiResponse(responseCode = "409", description = "Order already rated")
    })
    public ResponseEntity<OrderResponse> rateOrder(
            @Parameter(description = "Unique identifier of the order to rate", required = true)
            @PathVariable UUID orderId,
            @Parameter(description = "Rating details with score and optional comment", required = true)
            @RequestBody OrderRatingRequest request) {
        OrderResponse result = orderRatingHandler.rateOrder(orderId, request);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/statistics")
    @Operation(summary = "Get order statistics", description = "Retrieves order statistics including revenue, order counts, and analytics. Can be filtered by customer ID and date range")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Statistics retrieved successfully",
                    content = @Content(schema = @Schema(implementation = OrderStatisticsResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid date range or customer ID format")
    })
    public ResponseEntity<OrderStatisticsResponse> getStatistics(
            @Parameter(description = "Optional customer ID to filter statistics for specific customer")
            @RequestParam(required = false) UUID customerId,
            @Parameter(description = "Start date for statistics period (ISO format: yyyy-MM-dd)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date for statistics period (ISO format: yyyy-MM-dd)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        OrderStatisticsResponse result = orderStatisticsHandler.getStatistics(customerId, startDate, endDate);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/payment")
    @Operation(summary = "Process payment for an order", description = "Initiates payment processing for an order via Kafka messaging and returns SSE endpoint for status updates")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Payment request accepted and queued for processing",
                    content = @Content(schema = @Schema(implementation = OrderPaymentResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<OrderPaymentResponse> processOrderPayment(
            @Parameter(description = "Payment processing request with customer ID, order ID, and price", required = true)
            @Valid @RequestBody RestOrderPaymentRequest request) {

        OrderPaymentResponse result = orderPaymentHandler.processOrderPayment(request.toOrderPaymentRequest());
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(result);
    }

    @GetMapping(value = "/payment/status/{orderId}", produces = "text/event-stream")
    @Operation(summary = "Get payment status updates via SSE", description = "Provides Server-Sent Events stream for real-time payment status updates")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "SSE stream established successfully"),
            @ApiResponse(responseCode = "404", description = "Invalid order ID format")
    })
    public SseEmitter getPaymentStatusUpdates(
            @Parameter(description = "Unique identifier of the order to track payment status for", required = true)
            @PathVariable UUID orderId) {

        return paymentStatusSseService.createEmitter(orderId.toString());
    }
}