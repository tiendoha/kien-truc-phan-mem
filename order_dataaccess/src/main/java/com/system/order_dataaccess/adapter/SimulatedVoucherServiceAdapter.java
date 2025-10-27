package com.system.order_dataaccess.adapter;

import com.system.order_application_service.ports.VoucherServicePort;
import com.system.order_domain_core.valueobject.Voucher;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SimulatedVoucherServiceAdapter implements VoucherServicePort {

    private static final Map<String, Voucher> voucherDatabase = new ConcurrentHashMap<>();

    static {
        voucherDatabase.put("SALE10K", new Voucher("SALE10K", new BigDecimal("10000.00")));
        voucherDatabase.put("SALE50K", new Voucher("SALE50K", new BigDecimal("50000.00")));
    }

    @Override
    public Optional<Voucher> validateAndGetVoucher(String voucherCode) {
        Voucher voucher = voucherDatabase.get(voucherCode.toUpperCase());
        return Optional.ofNullable(voucher);
    }
}