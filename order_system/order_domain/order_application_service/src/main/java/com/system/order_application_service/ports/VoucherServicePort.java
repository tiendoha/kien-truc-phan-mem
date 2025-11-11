package com.system.order_application_service.ports;

import com.system.order_domain_core.valueobject.Voucher;
import java.util.Optional;

public interface VoucherServicePort {
    Optional<Voucher> validateAndGetVoucher(String voucherCode);
}