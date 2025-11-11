package com.system.order_messaging.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OrderPaymentRequest {
    private UUID orderId;
    private UUID customerId;
    private BigDecimal price; // Gửi giá cuối (đã trừ voucher)
}