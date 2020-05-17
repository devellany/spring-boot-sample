package com.devellany.sample.account.domain.enums;

import lombok.Getter;

@Getter
public enum AccountRole {
    MANAGER("manager"),
    STAFF("staff"),
    USER("user"),
    ;

    private final String tableValue;

    AccountRole(String tableValue) {
        this.tableValue = tableValue;
    }
}
