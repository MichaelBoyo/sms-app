package com.decoded.ussd.data.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class DepositRequest {
    private String phoneNumber;
    private BigDecimal amount;
}

