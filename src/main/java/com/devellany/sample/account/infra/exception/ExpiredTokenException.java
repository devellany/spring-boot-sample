package com.devellany.sample.account.infra.exception;

import com.devellany.sample.common.infra.handler.CustomException;

public class ExpiredTokenException extends CustomException {

    public ExpiredTokenException(String message) {
        super(message);
    }
}
