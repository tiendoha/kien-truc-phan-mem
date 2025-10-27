package com.system.order_dataaccess.adapter;

import com.system.order_application_service.ports.OrderRepository;
import com.system.order_dataaccess.entity.OrderEntity;
import com.system.order_dataaccess.mapper.OrderDataMapper;
import com.system.order_dataaccess.repository.OrderJpaRepository;
import com.system.order_domain_core.entity.Order;
import com.system.order_domain_core.valueobject.OrderStatistics;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class OrderRepositoryImpl implements OrderRepository {
    private final OrderJpaRepository orderJpaRepository;
    private final OrderDataMapper orderDataMapper;
    private final EntityManager entityManager;

    public OrderRepositoryImpl(OrderJpaRepository orderJpaRepository,
                               OrderDataMapper orderDataMapper,
                               EntityManager entityManager) {
        this.orderJpaRepository = orderJpaRepository;
        this.orderDataMapper = orderDataMapper;
        this.entityManager = entityManager;
    }

    @Override
    public Order save(Order order) {
        OrderEntity orderEntity = orderDataMapper.orderToOrderEntity(order);
        OrderEntity savedEntity = orderJpaRepository.save(orderEntity);
        return orderDataMapper.orderEntityToOrder(savedEntity);
    }

    @Override
    public List<Order> findByCustomerId(UUID customerId) {
        List<OrderEntity> entities = orderJpaRepository.findByCustomerId(customerId);
        return entities.stream()
                .map(orderDataMapper::orderEntityToOrder)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Order> findById(UUID orderId) {
        return orderJpaRepository.findById(orderId)
                .map(orderDataMapper::orderEntityToOrder);
    }

    @Override
    public OrderStatistics getStatistics(UUID customerId, ZonedDateTime startDate, ZonedDateTime endDate) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<OrderStatistics> query = cb.createQuery(OrderStatistics.class);
        Root<OrderEntity> order = query.from(OrderEntity.class);

        query.select(cb.construct(
                OrderStatistics.class,
                cb.count(order.get("id")),
                cb.sum(order.get("originalPrice"))
        ));

        List<Predicate> predicates = new ArrayList<>();
        if (customerId != null) {
            predicates.add(cb.equal(order.get("customerId"), customerId));
        }
        if (startDate != null) {
            predicates.add(cb.greaterThanOrEqualTo(order.get("createdAt"), startDate));
        }
        if (endDate != null) {
            predicates.add(cb.lessThanOrEqualTo(order.get("createdAt"), endDate));
        }
        query.where(cb.and(predicates.toArray(new Predicate[0])));

        TypedQuery<OrderStatistics> typedQuery = entityManager.createQuery(query);
        OrderStatistics result = typedQuery.getSingleResult();

        if (result == null) {
            return new OrderStatistics(0L, BigDecimal.ZERO);
        }
        return new OrderStatistics(
                result.getTotalOrders() != null ? result.getTotalOrders() : 0L,
                result.getTotalRevenue() != null ? result.getTotalRevenue() : BigDecimal.ZERO
        );
    }
}