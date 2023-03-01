package com.decoded.ussd.services;

import com.decoded.ussd.*;
import com.decoded.ussd.data.Menu;
import com.decoded.ussd.data.MenuOption;
import com.decoded.ussd.data.UssdSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.StringSubstitutor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class UssdRoutingService {
    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final MenuService menuService;
    private final SessionService sessionService;


    public String menuLevelRouter(String sessionId, String serviceCode, String phoneNumber, String text)
            throws IOException {
        if (!userRepository.existsUserByPhoneNumber(phoneNumber)) {
            User user = new User();
            user.setPhoneNumber(phoneNumber);
            user.setWallet(new Wallet());
            userRepository.save(user);
        }

        Map<String, Menu> menus = menuService.loadMenus();
        UssdSession session = checkAndSetSession(sessionId, serviceCode, phoneNumber, text);
        return text.length() > 0 ? getNextMenuItem(session, menus) : menus.get(session.getCurrentMenuLevel()).getText();
    }


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


    public String getMenu(String menuLevel) throws IOException {
        return menuService.loadMenus().get(menuLevel).getText();
    }


    public String processMenuOption(UssdSession session, MenuOption menuOption) throws IOException {
        switch (menuOption.getType()) {
            case "response":
                return processMenuOptionResponses(menuOption, session);
            case "level":
                updateSessionMenuLevel(session, menuOption.getNextMenuLevel());
                return getMenu(menuOption.getNextMenuLevel());
            default:
                return "CON ";
        }
    }


    public String processMenuOptionResponses(MenuOption menuOption, UssdSession session) {
        String response = menuOption.getResponse();
        Map<String, String> variablesMap = new HashMap<>();
        log.info("Deposit amount => {}", session.getText());
        User user = userRepository.findUserByPhoneNumber(session.getPhoneNumber()).orElse(null);
        assert user != null;
        switch (menuOption.getAction()) {
            case PROCESS_ACC_BALANCE -> variablesMap.put("account_balance", "10000");


            case PROCESS_DEPOSIT -> {

                Wallet wallet = user.getWallet();
                Transaction transaction = Transaction.builder()
                        .amount(BigDecimal.valueOf(1000))
                        .type(TransactionType.DEPOSIT)
                        .build();
                transaction = transactionRepository.save(transaction);
                wallet.getTransactionHistory().add(transaction);
                wallet = walletRepository.save(wallet);
                user.setWallet(wallet);
                userRepository.save(user);


                variablesMap.put("balance", String.valueOf(getBalance(user.getWallet())));
            }

            case PROCESS_ACC_PHONE_NUMBER -> variablesMap.put("phone_number", user.getPhoneNumber());

        }

        response = replaceVariable(variablesMap, response);
        return response;
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


    public String replaceVariable(Map<String, String> variablesMap, String response) {
        return new StringSubstitutor(variablesMap).replace(response);
    }

    public UssdSession updateSessionMenuLevel(UssdSession session, String menuLevel) {
        session.setPreviousMenuLevel(session.getCurrentMenuLevel());
        session.setCurrentMenuLevel(menuLevel);
        return sessionService.update(session);
    }


    public UssdSession checkAndSetSession(String sessionId, String serviceCode, String phoneNumber, String text) {
        UssdSession session = sessionService.get(sessionId);

        if (session != null) {
            session.setText(text);
            return sessionService.update(session);
        }

        session = new UssdSession();
        session.setCurrentMenuLevel("1");
        session.setPreviousMenuLevel("1");
        session.setId(sessionId);
        session.setPhoneNumber(phoneNumber);
        session.setServiceCode(serviceCode);
        session.setText(text);

        return sessionService.createUssdSession(session);
    }
}
