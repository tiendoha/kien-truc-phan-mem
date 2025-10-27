package com.system.order_container.rest;


import com.system.order_application_service.dto.CreateOrderRequest;
import com.system.order_application_service.dto.OrderResponse;
import com.system.order_application_service.handler.OrderCreateHandler;
import com.system.order_domain_core.entity.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value="/orders",produces="application/vnd.api.v1+json")
public class OrderController {
    private final OrderCreateHandler orderCreateHandler;

    public OrderController(OrderCreateHandler orderCreateHandler) {
        this.orderCreateHandler = orderCreateHandler;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@RequestBody CreateOrderRequest request) {
        OrderResponse result =  orderCreateHandler.createOrder(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(result);
    }


}