package com.decoded.ussd.data.repositories;

import com.decoded.ussd.data.models.Wallet;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface WalletRepository extends MongoRepository<Wallet, String> {
}
