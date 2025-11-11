package com.system.order_application_service.dto;

import java.math.BigDecimal;

public class OrderStatisticsResponse {
    private Long totalOrders;
    private BigDecimal totalRevenue;
    private BigDecimal averageOrderValue;

    public OrderStatisticsResponse(Long totalOrders, BigDecimal totalRevenue, BigDecimal averageOrderValue) {
        this.totalOrders = totalOrders;
        this.totalRevenue = totalRevenue;
        this.averageOrderValue = averageOrderValue;
    }

    public Long getTotalOrders() { return totalOrders; }
    public void setTotalOrders(Long totalOrders) { this.totalOrders = totalOrders; }
    public BigDecimal getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(BigDecimal totalRevenue) { this.totalRevenue = totalRevenue; }
    public BigDecimal getAverageOrderValue() { return averageOrderValue; }
    public void setAverageOrderValue(BigDecimal averageOrderValue) { this.averageOrderValue = averageOrderValue; }
}