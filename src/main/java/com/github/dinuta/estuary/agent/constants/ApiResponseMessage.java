package com.github.dinuta.estuary.agent.constants;

import java.util.HashMap;

public class ApiResponseMessage {
    private static final HashMap<Integer, String> message = new HashMap<>();

    static {
        message.put(ApiResponseCode.SUCCESS.code, "Success");
        message.put(ApiResponseCode.JINJA2_RENDER_FAILURE.code, "Jinja2 render failed");
        message.put(ApiResponseCode.GET_FILE_FAILURE.code, "Getting file or folder from the estuary agent service failed");
        message.put(ApiResponseCode.COMMAND_DETACHED_START_FAILURE.code, "Starting test id %s failed");
        message.put(ApiResponseCode.TEST_STOP_FAILURE.code, "Stopping running test %s failed");
        message.put(ApiResponseCode.GET_FILE_FAILURE_IS_DIR.code, "Getting %s from %s failed. It is a directory, not a file.");
        message.put(ApiResponseCode.GET_ENV_VAR_FAILURE.code, "Getting env var %s failed.");
        message.put(ApiResponseCode.MISSING_PARAMETER_POST.code, "Body parameter \"%s\" sent in request missing. Please include parameter. E.g. {\"parameter\"); \"value\"}");
        message.put(ApiResponseCode.FOLDER_ZIP_FAILURE.code, "Failed to zip folder %s.");
        message.put(ApiResponseCode.EMPTY_REQUEST_BODY_PROVIDED.code, "Empty request body provided.");
        message.put(ApiResponseCode.UPLOAD_FILE_FAILURE.code, "Failed to upload test configuration.");
        message.put(ApiResponseCode.HTTP_HEADER_NOT_PROVIDED.code, "Http header value not provided, '%s'");
        message.put(ApiResponseCode.COMMAND_EXEC_FAILURE.code, "Starting command(s) failed");
        message.put(ApiResponseCode.EXEC_COMMAND_NOT_ALLOWED.code, "'rm' commands are filtered. Command '%s' was not executed.");
        message.put(ApiResponseCode.UNAUTHORIZED.code, "Unauthorized");
        message.put(ApiResponseCode.SET_ENV_VAR_FAILURE.code, "Failed to set env vars \"%s\"");
        message.put(ApiResponseCode.INVALID_JSON_PAYLOAD.code, "Invalid json body \"%s\"");
        message.put(ApiResponseCode.NOT_IMPLEMENTED.code, "Not implemented");
    }

    public static String getMessage(int apiResponseCode) {
        return message.get(apiResponseCode);
    }
}
