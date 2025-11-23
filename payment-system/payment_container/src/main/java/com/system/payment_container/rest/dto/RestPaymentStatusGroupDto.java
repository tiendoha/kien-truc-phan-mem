package com.system.payment_container.rest.dto;

import com.system.payment_domain_core.valueobject.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RestPaymentStatusGroupDto {
    private PaymentStatus status;
    private int count;
    private BigDecimal totalAmount;
    private List<RestPaymentSummaryDto> payments;

    public static RestPaymentStatusGroupDto fromPaymentStatusGroupDto(
            com.system.payment_application_service.dto.PaymentStatusGroupDto groupDto) {
        return RestPaymentStatusGroupDto.builder()
                .status(groupDto.getStatus())
                .count(groupDto.getCount())
                .totalAmount(groupDto.getTotalAmount())
                .payments(groupDto.getPayments().stream()
                        .map(RestPaymentSummaryDto::fromPaymentSummaryDto)
                        .toList())
                .build();
    }
}