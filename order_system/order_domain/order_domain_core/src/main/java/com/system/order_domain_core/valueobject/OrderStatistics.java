package com.system.order_domain_core.valueobject;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class OrderStatistics {
    private final Long totalOrders;
    private final BigDecimal totalRevenue;

    public OrderStatistics(Long totalOrders, BigDecimal totalRevenue) {
        this.totalOrders = totalOrders != null ? totalOrders : 0L;
        this.totalRevenue = totalRevenue != null ? totalRevenue : BigDecimal.ZERO;
    }

    public Long getTotalOrders() { return totalOrders; }
    public BigDecimal getTotalRevenue() { return totalRevenue; }

    public BigDecimal getAverageOrderValue() {
        if (totalOrders == 0 || totalRevenue.equals(BigDecimal.ZERO)) {
            return BigDecimal.ZERO;
        }
        return totalRevenue.divide(BigDecimal.valueOf(totalOrders), 2, RoundingMode.HALF_UP);
    }
}