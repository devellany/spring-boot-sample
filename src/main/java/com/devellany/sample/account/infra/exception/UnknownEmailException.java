package com.devellany.sample.account.infra.exception;

import com.devellany.sample.common.infra.handler.CustomException;

public class UnknownEmailException extends CustomException {

    public UnknownEmailException(String message) {
        super(message);
    }
}
