package com.github.dinuta.estuary.agent.constants;

import java.util.HashMap;

public class ApiResponseMessage {
    private static final HashMap<Integer, String> message = new HashMap<>();

    static {
        message.put(ApiResponseConstants.SUCCESS, "Success");
        message.put(ApiResponseConstants.JINJA2_RENDER_FAILURE, "Jinja2 render failed");
        message.put(ApiResponseConstants.GET_FILE_FAILURE, "Getting file or folder from the estuary agent service failed");
        message.put(ApiResponseConstants.COMMAND_DETACHED_START_FAILURE, "Starting test id %s failed");
        message.put(ApiResponseConstants.TEST_STOP_FAILURE, "Stopping running test %s failed");
        message.put(ApiResponseConstants.GET_FILE_FAILURE_IS_DIR, "Getting %s from %s failed. It is a directory, not a file.");
        message.put(ApiResponseConstants.GET_ENV_VAR_FAILURE, "Getting env var %s failed.");
        message.put(ApiResponseConstants.MISSING_PARAMETER_POST, "Body parameter \"%s\" sent in request missing. Please include parameter. E.g. {\"parameter\"); \"value\"}");
        message.put(ApiResponseConstants.FOLDER_ZIP_FAILURE, "Failed to zip folder %s.");
        message.put(ApiResponseConstants.EMPTY_REQUEST_BODY_PROVIDED, "Empty request body provided.");
        message.put(ApiResponseConstants.UPLOAD_FILE_FAILURE, "Failed to upload test configuration.");
        message.put(ApiResponseConstants.HTTP_HEADER_NOT_PROVIDED, "Http header value not provided, '%s'");
        message.put(ApiResponseConstants.COMMAND_EXEC_FAILURE, "Starting command(s) failed");
        message.put(ApiResponseConstants.EXEC_COMMAND_NOT_ALLOWED, "'rm' commands are filtered. Command '%s' was not executed.");
        message.put(ApiResponseConstants.UNAUTHORIZED, "Unauthorized");
        message.put(ApiResponseConstants.SET_ENV_VAR_FAILURE, "Failed to set env vars \"%s\"");
        message.put(ApiResponseConstants.INVALID_JSON_PAYLOAD, "Invalid json body \"%s\"");
        message.put(ApiResponseConstants.NOT_IMPLEMENTED, "Not implemented");
    }

    public static String getMessage(int apiResponseCode) {
        return message.get(apiResponseCode);
    }
}
