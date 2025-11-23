package com.system.payment_dataaccess.repository;

import com.system.payment_dataaccess.entity.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentJpaRepository extends JpaRepository<PaymentEntity, UUID> {
    Optional<PaymentEntity> findByOrderId(UUID orderId);

    List<PaymentEntity> findByCustomerId(UUID customerId);

    List<PaymentEntity> findAll();

    @Query("SELECT p FROM PaymentEntity p WHERE (:customerId IS NULL OR p.customerId = :customerId)")
    List<PaymentEntity> findByCustomerIdOptional(@Param("customerId") UUID customerId);
}