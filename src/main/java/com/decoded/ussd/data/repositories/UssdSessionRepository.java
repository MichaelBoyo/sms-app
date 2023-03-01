package com.decoded.ussd.data.repositories;

import com.decoded.ussd.data.models.UssdSession;


import org.springframework.data.repository.CrudRepository;

public interface UssdSessionRepository extends CrudRepository<UssdSession, String> {
}