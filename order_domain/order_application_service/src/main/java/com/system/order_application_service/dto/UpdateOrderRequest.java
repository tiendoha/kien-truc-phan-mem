package com.system.order_application_service.dto;

import java.util.List;

public class UpdateOrderRequest {
    private List<OrderItemRequest> items;
    private String voucherCode;

    public List<OrderItemRequest> getItems() { return items; }
    public void setItems(List<OrderItemRequest> items) { this.items = items; }

    public String getVoucherCode() { return voucherCode; }
    public void setVoucherCode(String voucherCode) { this.voucherCode = voucherCode; }
}