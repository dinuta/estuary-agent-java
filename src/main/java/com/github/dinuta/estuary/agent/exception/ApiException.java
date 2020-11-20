package com.github.dinuta.estuary.agent.exception;

public class ApiException extends RuntimeException {
    private int code;

    public ApiException(int code, String msg) {
        super(msg);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
