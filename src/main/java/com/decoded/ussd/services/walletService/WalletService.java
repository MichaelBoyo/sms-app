package com.decoded.ussd.services.walletService;

import com.decoded.ussd.data.dtos.DepositRequest;
import com.decoded.ussd.data.dtos.GetBalanceRequest;
import com.decoded.ussd.data.dtos.WithdrawalRequest;
import com.decoded.ussd.data.models.Wallet;

import java.math.BigDecimal;

public interface WalletService {
    void deposit(DepositRequest depositRequest);
    void withDraw(WithdrawalRequest withdrawalRequest);

    BigDecimal getBalance(GetBalanceRequest getBalanceRequest);

    Wallet save(Wallet wallet);

    boolean hasEnoughMoney(BigDecimal valueOf, String phoneNumber);
}
