package com.github.dinuta.estuary.agent.constants;

public enum ApiResponseCode {
    SUCCESS(1000),
    GET_FILE_FAILURE(1001),
    COMMAND_START_FAILURE(1002),
    FOLDER_ZIP_FAILURE(1003),
    GET_FILE_FAILURE_IS_DIR(1004),
    GET_ENV_VAR_FAILURE(1005),
    MISSING_PARAMETER_POST(1006),
    GET_COMMAND_INFO_FAILURE(1007),
    EMPTY_REQUEST_BODY_PROVIDED(1008),
    COMMAND_STOP_FAILURE(1009),
    UPLOAD_FILE_FAILURE(1010),
    HTTP_HEADER_NOT_PROVIDED(1011),
    COMMAND_EXEC_FAILURE(1012),
    EXEC_COMMAND_NOT_ALLOWED(1013),
    UNAUTHORIZED(1014),
    SET_ENV_VAR_FAILURE(1015),
    INVALID_JSON_PAYLOAD(1016),
    NOT_IMPLEMENTED(1017),
    GENERAL(1100);

    private final int code;

    public int getCode() {
        return code;
    }

    private ApiResponseCode(int code) {
        this.code = code;
    }
}
