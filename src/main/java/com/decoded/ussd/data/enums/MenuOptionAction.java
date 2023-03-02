package com.decoded.ussd.data.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum MenuOptionAction {
    PROCESS_ACC_NUMBER("PROCESS_ACC_NUMBER"),
    PROCESS_BALANCE("PROCESS_BALANCE"),
    PROCESS_WITHDRAW("PROCESS_WITHDRAW"),
    PROCESS_WITHDRAW_5("PROCESS_WITHDRAW_5"),
    PROCESS_WITHDRAW_30("PROCESS_WITHDRAW_30"),


    PROCESS_DEPOSIT("PROCESS_DEPOSIT"),
    PROCESS_DEPOSIT_5("PROCESS_DEPOSIT_5"),
    PROCESS_DEPOSIT_30("PROCESS_DEPOSIT_30");

    private String action;

    MenuOptionAction(String action) {
        this.action = action;
    }

    @JsonValue
    private String getAction() {
        return action;
    }
}
