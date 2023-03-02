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
import com.decoded.ussd.services.userService.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {

    private final UserService userService;
    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;

    @Override
    public void deposit(DepositRequest depositRequest) {
        transact(TransactionType.DEPOSIT,
                depositRequest.getPhoneNumber(), depositRequest.getAmount());
    }

    @Transactional
    public void transact(TransactionType type, String phone, BigDecimal amount) {
        User user = userService.getUserByPhoneNumber(phone);
        Wallet wallet = user.getWallet();
        Transaction transaction = Transaction.builder()
                .amount(amount)
                .type(type)
                .build();
        transaction = transactionRepository.save(transaction);
        wallet.getTransactionHistory().add(transaction);
        wallet = walletRepository.save(wallet);
        user.setWallet(wallet);
        userService.save(user);
    }


    @Override
    public void withDraw(WithdrawalRequest withdrawalRequest) {
        transact(TransactionType.WITHDRAWAL,
                withdrawalRequest.getPhoneNumber(), withdrawalRequest.getAmount());
    }


    @Override
    public BigDecimal getBalance(GetBalanceRequest getBalanceRequest) {
        Wallet wallet = userService.getUserByPhoneNumber(getBalanceRequest.getPhoneNumber()).getWallet();
        return getBalance(wallet);

    }

    @Override
    public Wallet save(Wallet wallet) {
        return walletRepository.save(wallet);
    }

    @Override
    public boolean hasEnoughMoney(BigDecimal amount, String phoneNumber) {
        Wallet wallet = userService.getUserByPhoneNumber(phoneNumber).getWallet();
        return getBalance(wallet).compareTo(amount) >= 0;
    }


    private BigDecimal getBalance(Wallet wallet) {
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
