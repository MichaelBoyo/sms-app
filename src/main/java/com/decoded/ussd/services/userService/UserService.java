package com.decoded.ussd.services.userService;

import com.decoded.ussd.data.models.User;

public interface UserService {
    void save(User user);

    boolean existsUserByPhoneNumber(String phoneNumber);

    String getAccountNumber(String phoneNumber);

    User getUserByPhoneNumber(String phoneNumber);
}
