package com.system.order_application_service.exception;

// Exception khi không tìm thấy đơn hàng
public class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException(String message) {
        super(message);
    }
}