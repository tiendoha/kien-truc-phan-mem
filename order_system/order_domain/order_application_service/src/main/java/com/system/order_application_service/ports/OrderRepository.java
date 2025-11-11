package com.system.order_application_service.ports;
import com.system.order_domain_core.entity.Order;
import com.system.order_domain_core.valueobject.OrderStatistics; // <-- THÊM IMPORT

import java.time.ZonedDateTime; // <-- THÊM IMPORT
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository {
    Order save(Order order);
    List<Order> findByCustomerId(UUID customerId);
    Optional<Order> findById(UUID orderId);

    OrderStatistics getStatistics(UUID customerId, ZonedDateTime startDate, ZonedDateTime endDate);
}