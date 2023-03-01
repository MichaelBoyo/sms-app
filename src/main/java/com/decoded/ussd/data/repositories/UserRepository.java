package com.decoded.ussd.data.repositories;

import com.decoded.ussd.data.models.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findUserByPhoneNumber(String phoneNumber);

    boolean existsUserByPhoneNumber(String phoneNumber);
}
