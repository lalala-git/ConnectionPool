package com.jdbc.exception;

public class ConnectionPoolException extends Exception {

    public ConnectionPoolException() {}

    public ConnectionPoolException(String sql) {
        super(sql);
    }

}
