package com.decoded.ussd.services.routingService;

import com.decoded.ussd.data.models.User;
import com.decoded.ussd.data.models.Wallet;
import com.decoded.ussd.data.models.Menu;
import com.decoded.ussd.data.models.MenuOption;
import com.decoded.ussd.data.models.UssdSession;
import com.decoded.ussd.data.dtos.DepositRequest;
import com.decoded.ussd.data.dtos.GetBalanceRequest;
import com.decoded.ussd.data.dtos.WithdrawalRequest;
import com.decoded.ussd.services.sessionService.SessionServiceImpl;
import com.decoded.ussd.services.userService.UserService;
import com.decoded.ussd.services.walletService.WalletService;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.StringSubstitutor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class UssdRoutingService implements RoutingService {
    private final WalletService walletService;
    private final UserService userService;

    @Value("withdraw.message.success")
    private String successWithDrawMessage;

    @Value("withdraw.message.fail")
    private String failWithdrawMessage;


    private final com.decoded.ussd.services.menuService.iMenuService iMenuService;
    private final SessionServiceImpl sessionServiceImpl;


    @Override
    public String menuLevelRouter(String sessionId, String serviceCode, String phoneNumber, String text)
            throws IOException {
        if (!userService.existsUserByPhoneNumber(phoneNumber)) {
            User user = new User();
            user.setPhoneNumber(phoneNumber);
            Wallet wallet = Wallet.builder()
                    .accountNo(phoneNumber.substring(phoneNumber.length() - 10))
                    .build();
            wallet = walletService.save(wallet);
            user.setWallet(wallet);
            userService.save(user);
        }

        Map<String, Menu> menus = iMenuService.loadMenus();
        UssdSession session = checkAndSetSession(sessionId, serviceCode, phoneNumber, text);
        return text.length() > 0 ? getNextMenuItem(session, menus) : menus.get(session.getCurrentMenuLevel()).getText();
    }


    @Override
    public String getNextMenuItem(UssdSession session, Map<String, Menu> menus) throws IOException {
        String[] levels = session.getText().split("\\*");
        String lastValue = levels[levels.length - 1];
        Menu menuLevel = menus.get(session.getCurrentMenuLevel());

        if (Integer.parseInt(lastValue) <= menuLevel.getMaxSelections()) {
            MenuOption menuOption = menuLevel.getMenuOptions().get(Integer.parseInt(lastValue) - 1);
            return processMenuOption(session, menuOption);
        }

        return "CON ";
    }


    @Override
    public String getMenu(String menuLevel) throws IOException {
        return iMenuService.loadMenus().get(menuLevel).getText();
    }


    @Override
    public String processMenuOption(UssdSession session, MenuOption menuOption) throws IOException {
        switch (menuOption.getType()) {
            case "response" -> {
                return processMenuOptionResponses(menuOption, session);
            }
            case "level" -> {
                updateSessionMenuLevel(session, menuOption.getNextMenuLevel());
                return getMenu(menuOption.getNextMenuLevel());
            }
            default -> {
                return "CON ";
            }
        }
    }


    @Override
    public String processMenuOptionResponses(MenuOption menuOption, UssdSession session) {
        String response = menuOption.getResponse();
        Map<String, String> variablesMap = new HashMap<>();
        switch (menuOption.getAction()) {
            case PROCESS_WITHDRAW -> perform_withdrawal(1000.0, variablesMap, session);
            case PROCESS_WITHDRAW_5 -> perform_withdrawal(5000.0, variablesMap, session);
            case PROCESS_WITHDRAW_30 -> perform_withdrawal(30000.0, variablesMap, session);
            case PROCESS_BALANCE -> returnBalance(session, variablesMap);
            case PROCESS_DEPOSIT -> performDeposit(session, variablesMap, 1000.0);
            case PROCESS_DEPOSIT_5 -> performDeposit(session, variablesMap, 5000.0);
            case PROCESS_DEPOSIT_30 -> performDeposit(session, variablesMap, 30000.0);
            case PROCESS_ACC_NUMBER ->
                    variablesMap.put("account_number", userService.getAccountNumber(session.getPhoneNumber()));

        }

        response = replaceVariable(variablesMap, response);
        return response;
    }

    private void performDeposit(UssdSession session, Map<String, String> variablesMap, double amount) {
        deposit(session, amount);
        returnBalance(session, variablesMap);
    }

    private void perform_withdrawal(Double amount, Map<String, String> variablesMap, UssdSession session) {
        if (walletService.hasEnoughMoney(BigDecimal.valueOf(amount), session.getPhoneNumber())) {
            withdraw(session, amount);
            returnBalance(session, variablesMap, String.format(successWithDrawMessage, amount));
        } else {
            returnBalance(session, variablesMap, failWithdrawMessage);
        }
    }

    private void returnBalance(UssdSession session, Map<String, String> variablesMap, String message) {
        variablesMap.put("balance", message + walletService.getBalance(GetBalanceRequest.builder()
                .phoneNumber(session.getPhoneNumber())
                .build()));
    }


    private void withdraw(UssdSession session, double amount) {
        walletService.withDraw(WithdrawalRequest.builder()
                .phoneNumber(session.getPhoneNumber())
                .amount(BigDecimal.valueOf(amount))
                .build());
    }

    private void returnBalance(UssdSession session, Map<String, String> variablesMap) {
        variablesMap.put("balance", String.valueOf(
                walletService.getBalance(GetBalanceRequest.builder()
                        .phoneNumber(session.getPhoneNumber())
                        .build())));
    }

    private void deposit(UssdSession session, Double amount) {
        walletService.deposit(DepositRequest.builder()
                .phoneNumber(session.getPhoneNumber())
                .amount(BigDecimal.valueOf(amount))
                .build());
    }


    @Override
    public String replaceVariable(Map<String, String> variablesMap, String response) {
        return new StringSubstitutor(variablesMap).replace(response);
    }

    @Override
    public UssdSession updateSessionMenuLevel(UssdSession session, String menuLevel) {
        session.setPreviousMenuLevel(session.getCurrentMenuLevel());
        session.setCurrentMenuLevel(menuLevel);
        return sessionServiceImpl.update(session);
    }


    @Override
    public UssdSession checkAndSetSession(String sessionId, String serviceCode, String phoneNumber, String text) {
        UssdSession session = sessionServiceImpl.getUssdSession(sessionId);

        if (session != null) {
            session.setText(text);
            return sessionServiceImpl.update(session);
        }

        session = new UssdSession();
        session.setCurrentMenuLevel("1");
        session.setPreviousMenuLevel("1");
        session.setId(sessionId);
        session.setPhoneNumber(phoneNumber);
        session.setServiceCode(serviceCode);
        session.setText(text);

        return sessionServiceImpl.createUssdSession(session);
    }
}
