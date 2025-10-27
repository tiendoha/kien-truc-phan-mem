package com.system.order_container.rest;

import com.system.order_application_service.dto.CreateOrderRequest;
import com.system.order_application_service.dto.OrderRatingRequest;
import com.system.order_application_service.dto.OrderResponse;
import com.system.order_application_service.dto.OrderStatisticsResponse;
import com.system.order_application_service.dto.UpdateOrderRequest;
import com.system.order_application_service.handler.OrderCreateHandler;
import com.system.order_application_service.handler.OrderQueryHandler;
import com.system.order_application_service.handler.OrderRatingHandler;
import com.system.order_application_service.handler.OrderStatisticsHandler;
import com.system.order_application_service.handler.OrderUpdateHandler;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(value="/orders",produces="application/vnd.api.v1+json")
public class OrderController {

    private final OrderCreateHandler orderCreateHandler;
    private final OrderQueryHandler orderQueryHandler;
    private final OrderUpdateHandler orderUpdateHandler;
    private final OrderRatingHandler orderRatingHandler; // <-- (Feature 5)
    private final OrderStatisticsHandler orderStatisticsHandler; // <-- (Feature 3)

    public OrderController(OrderCreateHandler orderCreateHandler,
                           OrderQueryHandler orderQueryHandler,
                           OrderUpdateHandler orderUpdateHandler,
                           OrderRatingHandler orderRatingHandler,
                           OrderStatisticsHandler orderStatisticsHandler) {
        this.orderCreateHandler = orderCreateHandler;
        this.orderQueryHandler = orderQueryHandler;
        this.orderUpdateHandler = orderUpdateHandler;
        this.orderRatingHandler = orderRatingHandler;
        this.orderStatisticsHandler = orderStatisticsHandler;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@RequestBody CreateOrderRequest request) {
        OrderResponse result =  orderCreateHandler.createOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getOrdersByCustomerId(
            @RequestParam(name = "customerId") UUID customerId) {
        List<OrderResponse> result = orderQueryHandler.getOrdersByCustomerId(customerId);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/{orderId}")
    public ResponseEntity<OrderResponse> updateOrder(
            @PathVariable UUID orderId,
            @RequestBody UpdateOrderRequest request) {
        OrderResponse result = orderUpdateHandler.updateOrder(orderId, request);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/{orderId}/rating")
    public ResponseEntity<OrderResponse> rateOrder(
            @PathVariable UUID orderId,
            @RequestBody OrderRatingRequest request) {
        OrderResponse result = orderRatingHandler.rateOrder(orderId, request);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/statistics")
    public ResponseEntity<OrderStatisticsResponse> getStatistics(
            @RequestParam(required = false) UUID customerId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        OrderStatisticsResponse result = orderStatisticsHandler.getStatistics(customerId, startDate, endDate);
        return ResponseEntity.ok(result);
    }
}