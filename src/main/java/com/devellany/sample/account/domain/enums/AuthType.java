package com.devellany.sample.account.domain.enums;

import lombok.Getter;

@Getter
public enum AuthType {
    EMAIL("email"),
    ;

    private final String tableValue;

    AuthType(String tableValue) {
        this.tableValue = tableValue;
    }
}