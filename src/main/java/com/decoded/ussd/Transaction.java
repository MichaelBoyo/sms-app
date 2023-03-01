package com.decoded.ussd;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Document
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class Transaction {
    @Id
    private String id;
    private TransactionType type;
    private BigDecimal amount;
    private LocalDateTime localDateTime = LocalDateTime.now();
}
