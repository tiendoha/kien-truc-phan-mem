package com.system.order_dataaccess.adapter;

import com.system.order_application_service.ports.OrderRepository;
import com.system.order_dataaccess.entity.OrderEntity;
import com.system.order_dataaccess.mapper.OrderDataMapper;
import com.system.order_dataaccess.repository.OrderJpaRepository;
import com.system.order_domain_core.entity.Order;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class OrderRepositoryImpl implements OrderRepository {
    private final OrderJpaRepository orderJpaRepository;
    private final OrderDataMapper orderDataMapper;

    public OrderRepositoryImpl(OrderJpaRepository orderJpaRepository, OrderDataMapper orderDataMapper) {
        this.orderJpaRepository = orderJpaRepository;
        this.orderDataMapper = orderDataMapper;
    }

    @Override
    public Order save(Order order) {
        OrderEntity orderEntity = orderDataMapper.orderToOrderEntity(order);
        return orderDataMapper.orderEntityToOrder(orderJpaRepository.save(orderEntity));
    }
}