package com.jdbc.exception;

public class NoParamsException extends Exception {
    public NoParamsException() {}

    public NoParamsException(String msg) {
        super(msg);
    }
}
