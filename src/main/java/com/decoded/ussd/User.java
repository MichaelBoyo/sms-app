package com.decoded.ussd;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
@AllArgsConstructor
@RequiredArgsConstructor
public class User {
    @Id
    private String id;
    private Wallet wallet;
    private String name;
    private String phoneNumber;
}
