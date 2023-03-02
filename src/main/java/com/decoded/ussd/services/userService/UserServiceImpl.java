package com.decoded.ussd.services.userService;

import com.decoded.ussd.data.models.User;
import com.decoded.ussd.data.repositories.UserRepository;
import com.decoded.ussd.services.walletService.UssdException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public void save(User user) {

        userRepository.save(user);
    }

    @Override
    public boolean existsUserByPhoneNumber(String phoneNumber) {
        return userRepository.existsUserByPhoneNumber(phoneNumber);
    }

    @Override
    public String getAccountNumber(String phoneNumber) {

        return getUserByPhoneNumber(phoneNumber).getWallet().getAccountNo();
    }

    @Override
    public User getUserByPhoneNumber(String phoneNumber) {
        return userRepository.findUserByPhoneNumber(phoneNumber).orElseThrow(
                () -> new UssdException("user not found")
        );
    }
}
