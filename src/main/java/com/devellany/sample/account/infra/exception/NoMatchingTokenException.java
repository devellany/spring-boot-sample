package com.devellany.sample.account.infra.exception;

import com.devellany.sample.common.infra.handler.CustomException;

public class NoMatchingTokenException extends CustomException {

    public NoMatchingTokenException(String message) {
        super(message);
    }
}
