package com.system.order_dataaccess.repository;

import com.system.order_dataaccess.entity.OrderEntity;
import com.system.order_domain_core.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
@Repository
public interface OrderJpaRepository extends JpaRepository<OrderEntity, UUID> {
}