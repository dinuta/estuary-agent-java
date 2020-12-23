package com.github.dinuta.estuary.agent.constants;

public enum ApiResponseCode {
    SUCCESS(1000),
    JINJA2_RENDER_FAILURE(1001),
    GET_FILE_FAILURE(1002),
    COMMAND_DETACHED_START_FAILURE(1003),
    FOLDER_ZIP_FAILURE(1005),
    GET_FILE_FAILURE_IS_DIR(1006),
    GET_ENV_VAR_FAILURE(1007),
    MISSING_PARAMETER_POST(1008),
    GET_TEST_INFO_FAILURE(1009),
    EMPTY_REQUEST_BODY_PROVIDED(1010),
    TEST_STOP_FAILURE(1011),
    UPLOAD_FILE_FAILURE(1012),
    HTTP_HEADER_NOT_PROVIDED(1013),
    COMMAND_EXEC_FAILURE(1014),
    EXEC_COMMAND_NOT_ALLOWED(1015),
    UNAUTHORIZED(1016),
    SET_ENV_VAR_FAILURE(1017),
    INVALID_JSON_PAYLOAD(1018),
    NOT_IMPLEMENTED(1019),
    GENERAL(1100);

    private final int code;

    public int getCode() {
        return code;
    }

    private ApiResponseCode(int code) {
        this.code = code;
    }
}
