package com.system.order_application_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Response object containing order statistics and analytics")
public class OrderStatisticsResponse {
    @Schema(description = "Total number of orders in the specified period", example = "150")
    private Long totalOrders;

    @Schema(description = "Total revenue generated from orders", example = "4250.75")
    private BigDecimal totalRevenue;

    @Schema(description = "Average order value (total revenue / total orders)", example = "28.34")
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