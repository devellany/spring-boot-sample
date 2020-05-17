package com.devellany.sample.account.domain.enums;

import lombok.Getter;

@Getter
public enum VerifiedStatus {
    CONFIRM("confirm"),
    CANCEL("cancel"),
    TIMEOUT("timeout"),
    WAITING("waiting"),
    ;

    private final String tableValue;

    VerifiedStatus(String tableValue) {
        this.tableValue = tableValue;
    }
}
