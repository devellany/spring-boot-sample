package com.devellany.sample.account.infra.exception;

import com.devellany.sample.common.infra.handler.CustomException;

public class AlreadyConfirmTokenException extends CustomException {

    public AlreadyConfirmTokenException(String message) {
        super(message);
    }
}