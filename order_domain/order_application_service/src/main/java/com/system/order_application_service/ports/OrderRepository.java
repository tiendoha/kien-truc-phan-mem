package com.system.order_application_service.ports;
import com.system.order_domain_core.entity.Order;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository {
    Order save(Order order);

}