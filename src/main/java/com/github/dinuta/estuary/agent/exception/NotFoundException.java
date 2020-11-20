package com.github.dinuta.estuary.agent.exception;

public class NotFoundException extends ApiException {
    private int code;

    @Override
    public int getCode() {
        return code;
    }

    public NotFoundException(int code, String msg) {
        super(code, msg);
        this.code = code;
    }
}
