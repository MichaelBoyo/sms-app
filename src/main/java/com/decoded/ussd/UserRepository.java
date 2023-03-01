package com.decoded.ussd;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findUserByPhoneNumber(String phoneNumber);

    boolean existsUserByPhoneNumber(String phoneNumber);
}
