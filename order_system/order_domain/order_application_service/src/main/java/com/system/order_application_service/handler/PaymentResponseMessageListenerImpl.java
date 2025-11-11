package com.system.order_application_service.handler;

import com.system.order_application_service.dto.PaymentResponse;
import com.system.order_application_service.exception.OrderNotFoundException;
import com.system.order_application_service.ports.input.PaymentResponseMessageListener;
import com.system.order_application_service.ports.OrderRepository;
import com.system.order_domain_core.entity.Order;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
public class PaymentResponseMessageListenerImpl implements PaymentResponseMessageListener {

    private final OrderRepository orderRepository;

    public PaymentResponseMessageListenerImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    @Transactional
    public void paymentCompleted(PaymentResponse paymentResponse) {
        Order order = orderRepository.findById(paymentResponse.getOrderId())
                .orElseThrow(() -> new OrderNotFoundException("Order not found: " + paymentResponse.getOrderId()));
        order.pay();
        orderRepository.save(order);
    }

    @Override
    @Transactional
    public void paymentFailed(PaymentResponse paymentResponse) {
        Order order = orderRepository.findById(paymentResponse.getOrderId())
                .orElseThrow(() -> new OrderNotFoundException("Order not found: " + paymentResponse.getOrderId()));
        order.cancel(paymentResponse.getFailureMessage());
        orderRepository.save(order);
    }
}