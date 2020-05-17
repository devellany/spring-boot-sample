package com.devellany.sample.common.infra.handler;

public class CustomException extends RuntimeException {
    public CustomException(String message) {
        super(message);
    }

    public String getExceptionName() {
        return this.getClass().getSimpleName();
    }
}
