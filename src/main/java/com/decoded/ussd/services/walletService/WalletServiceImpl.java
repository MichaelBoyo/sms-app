package com.decoded.ussd.services.walletService;

import com.decoded.ussd.data.dtos.DepositRequest;
import com.decoded.ussd.data.dtos.GetBalanceRequest;
import com.decoded.ussd.data.dtos.WithdrawalRequest;
import com.decoded.ussd.data.enums.TransactionType;
import com.decoded.ussd.data.models.Transaction;
import com.decoded.ussd.data.models.User;
import com.decoded.ussd.data.models.Wallet;
import com.decoded.ussd.data.repositories.UserRepository;
import com.decoded.ussd.data.repositories.TransactionRepository;
import com.decoded.ussd.data.repositories.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {
    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;

    @Override
    public void deposit(DepositRequest depositRequest) {
        User user = getUserByPhoneNumber(depositRequest.getPhoneNumber());

        Wallet wallet = user.getWallet();
        Transaction transaction = Transaction.builder()
                .amount(depositRequest.getAmount())
                .type(TransactionType.DEPOSIT)
                .build();
        transaction = transactionRepository.save(transaction);
        wallet.getTransactionHistory().add(transaction);
        wallet = walletRepository.save(wallet);
        user.setWallet(wallet);
        userRepository.save(user);
    }

    private User getUserByPhoneNumber(String phoneNumber) {
        return userRepository.findUserByPhoneNumber(phoneNumber).orElseThrow(
                () -> new UssdException("user not found")
        );
    }

    @Override
    public void withDraw(WithdrawalRequest withdrawalRequest) {

    }


    @Override
    public BigDecimal getBalance(GetBalanceRequest getBalanceRequest) {
        Wallet wallet = getUserByPhoneNumber(getBalanceRequest.getPhoneNumber()).getWallet();
        BigDecimal bal = BigDecimal.ZERO;
        for (Transaction transaction : wallet.getTransactionHistory()) {
            switch (transaction.getType()) {
                case DEPOSIT -> bal = bal.add(transaction.getAmount());
                case WITHDRAWAL -> bal = bal.subtract(transaction.getAmount());
            }
        }

        return bal;
    }
}
