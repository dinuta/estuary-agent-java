package com.github.dinuta.estuary.agent.exception;

import com.github.dinuta.estuary.agent.constants.ApiResponseCode;

public class ApiException extends RuntimeException {
    private final ApiResponseCode code;

    private final Object[] params;

    public ApiException(ApiResponseCode code, Throwable cause, Object... params) {
        super(cause);
        this.code = code;
        this.params = params;
    }

    public ApiException(Exception cause) {
        this(ApiResponseCode.GENERAL, cause);
    }

    public Object[] getParams() {
        return params;
    }

    public ApiResponseCode getCode() {
        return code;
    }

}
