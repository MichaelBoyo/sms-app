package com.decoded.ussd.data.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;
@Data
@Document
@AllArgsConstructor
@RequiredArgsConstructor
public class Wallet {
    @Id
    private String walletId;

    private List<Transaction> transactionHistory = new ArrayList<>();
}
