package com.decoded.ussd.data.repositories;

import com.decoded.ussd.data.models.Transaction;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TransactionRepository extends MongoRepository<Transaction, String> {
}
