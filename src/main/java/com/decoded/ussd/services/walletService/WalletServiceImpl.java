package com.decoded.ussd.services.walletService;

import com.decoded.ussd.data.dtos.*;
import com.decoded.ussd.data.enums.TransactionType;
import com.decoded.ussd.data.models.Transaction;
import com.decoded.ussd.data.models.User;
import com.decoded.ussd.data.models.Wallet;
import com.decoded.ussd.data.repositories.TransactionRepository;
import com.decoded.ussd.data.repositories.WalletRepository;
import com.decoded.ussd.services.notification.SMSNotificationService;
import com.decoded.ussd.services.userService.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {

    private final UserService userService;
    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final SMSNotificationService smsNotificationService;

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
        String message = "of" + amount + "Successful";
        switch (type) {
            case DEPOSIT -> message = "Deposit " + message;
            case WITHDRAWAL -> message = "Withdrawal " + message;
        }

        smsNotificationService.sendSms(SmsRequest.builder()
                .messages(List.of(Message.builder()
                        .destinations(List.of(Destination.builder()
                                .to(phone)
                                .build()))
                        .from("ussd app")
                        .text(message)
                        .build()))
                .build());
    }

//    public static void main(String[] args) throws JsonProcessingException {
//        ObjectMapper objectMapper = new ObjectMapper();
//
//        SmsRequest message = SmsRequest.builder()
//                .messages(List.of(Message.builder()
//                        .destinations(List.of(Destination.builder()
//                                .to("0293848430")
//                                .build()))
//                        .from("ussd app")
//                        .text("test text")
//                        .build()))
//                .build();
//
//        System.out.println(objectMapper.writeValueAsString(message));
//        String bodyJson = String.format("{\"messages\":[{\"from\":\"%s\",\"destinations\":[{\"to\":\"%s\"}],\"text\":\"%s\"}]}",
//                "mike",
//                "0292933030",
//                "text"
//        );
//        System.out.println(bodyJson);
//    }


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
