package com.system.order_messaging.mapper;

import com.system.order_domain_core.entity.Order;
import com.system.order_messaging.dto.OrderPaymentRequest;
import org.springframework.stereotype.Component;

@Component
public class OrderMessagingDataMapper {

    public OrderPaymentRequest orderToOrderPaymentRequest(Order order) {
        return new OrderPaymentRequest(
                order.getId().getValue(),
                order.getCustomerId().getValue(),
                order.getPrice() // Gửi giá cuối (đã áp dụng voucher)
        );
    }
}