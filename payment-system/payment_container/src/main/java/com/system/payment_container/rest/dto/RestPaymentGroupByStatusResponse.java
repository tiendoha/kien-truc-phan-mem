package com.system.payment_container.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RestPaymentGroupByStatusResponse {
    private UUID customerId;
    private int totalCount;
    private BigDecimal totalAmount;
    private List<RestPaymentStatusGroupDto> statusGroups;

    public static RestPaymentGroupByStatusResponse fromPaymentGroupByStatusResponse(
            com.system.payment_application_service.dto.PaymentGroupByStatusResponse response) {
        return RestPaymentGroupByStatusResponse.builder()
                .customerId(response.getCustomerId())
                .totalCount(response.getTotalCount())
                .totalAmount(response.getTotalAmount())
                .statusGroups(response.getStatusGroups().stream()
                        .map(RestPaymentStatusGroupDto::fromPaymentStatusGroupDto)
                        .toList())
                .build();
    }
}