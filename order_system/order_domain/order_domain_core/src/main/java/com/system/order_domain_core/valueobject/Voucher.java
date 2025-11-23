package com.system.order_domain_core.valueobject;

import java.math.BigDecimal;

public class Voucher {
    private final String code;
    private final BigDecimal discountValue;

    public Voucher(String code, BigDecimal discountValue) {
        this.code = code;
        this.discountValue = discountValue;
    }

    public BigDecimal calculateDiscount(BigDecimal originalPrice) {
        return originalPrice.compareTo(discountValue) > 0 ? discountValue : originalPrice;
    }

    public String getCode() { return code; }
}