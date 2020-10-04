package com.github.dinuta.estuary.agent.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dinuta.estuary.agent.component.ClientRequest;
import com.github.dinuta.estuary.agent.component.VirtualEnvironment;
import com.github.dinuta.estuary.agent.constants.About;
import com.github.dinuta.estuary.agent.constants.ApiResponseConstants;
import com.github.dinuta.estuary.agent.constants.ApiResponseMessage;
import com.github.dinuta.estuary.agent.constants.DateTimeConstants;
import com.github.dinuta.estuary.agent.model.api.ApiResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@Api(tags = {"estuary-agent"})
@Controller
public class EnvApiController implements EnvApi {
    private static final Logger log = LoggerFactory.getLogger(EnvApiController.class);

    private final ObjectMapper objectMapper;
    private final HttpServletRequest request;

    @Autowired
    private VirtualEnvironment environment;

    @Autowired
    private ClientRequest clientRequest;

    @Autowired
    public EnvApiController(ObjectMapper objectMapper, HttpServletRequest request) {
        this.objectMapper = objectMapper;
        this.request = request;
    }

    public ResponseEntity<ApiResponse> envEnvNameGet(@ApiParam(value = "The name of the env var to get value from", required = true) @PathVariable("env_name") String envName, @ApiParam(value = "") @RequestHeader(value = "Token", required = false) String token) {
        String accept = request.getHeader("Accept");

        return new ResponseEntity<>(new ApiResponse()
                .code(ApiResponseConstants.SUCCESS)
                .message(ApiResponseMessage.getMessage(ApiResponseConstants.SUCCESS))
                .description(environment.getEnvironmentAndVirtualEnvironment().get(envName))
                .name(About.getAppName())
                .version(About.getVersion())
                .timestamp(LocalDateTime.now().format(DateTimeConstants.PATTERN))
                .path(clientRequest.getRequestUri()), HttpStatus.OK);
    }

    public ResponseEntity<ApiResponse> envGet(@ApiParam(value = "") @RequestHeader(value = "Token", required = false) String token) {
        String accept = request.getHeader("Accept");

        return new ResponseEntity<>(new ApiResponse()
                .code(ApiResponseConstants.SUCCESS)
                .message(ApiResponseMessage.getMessage(ApiResponseConstants.SUCCESS))
                .description(environment.getEnvironmentAndVirtualEnvironment())
                .name(About.getAppName())
                .version(About.getVersion())
                .timestamp(LocalDateTime.now().format(DateTimeConstants.PATTERN))
                .path(clientRequest.getRequestUri()), HttpStatus.OK);
    }

    public ResponseEntity<ApiResponse> envPost(@ApiParam(value = "List of env vars by key-value pair in JSON format", required = true) @Valid @RequestBody String envVars, @ApiParam(value = "Authentication Token") @RequestHeader(value = "Token", required = false) String token) {
        Map<String, String> envVarsToBeAdded = new LinkedHashMap<>();
        Map<String, String> virtualEnvVarsAdded = new LinkedHashMap<>();

        try {
            envVarsToBeAdded = objectMapper.readValue(envVars, LinkedHashMap.class);
            environment.setExternalEnvVars(envVarsToBeAdded);
        } catch (Exception e) {
            log.debug(ExceptionUtils.getStackTrace(e));
            return new ResponseEntity<>(new ApiResponse()
                    .code(ApiResponseConstants.SET_ENV_VAR_FAILURE)
                    .message(String.format(
                            ApiResponseMessage.getMessage(ApiResponseConstants.SET_ENV_VAR_FAILURE), envVars))
                    .description(ExceptionUtils.getStackTrace(e))
                    .name(About.getAppName())
                    .version(About.getVersion())
                    .timestamp(LocalDateTime.now().format(DateTimeConstants.PATTERN))
                    .path(clientRequest.getRequestUri()), HttpStatus.NOT_FOUND);
        }

        envVarsToBeAdded.forEach((key, value) -> {
            if (environment.getVirtualEnvironment().containsKey(key)) virtualEnvVarsAdded.put(key, value);
        });

        return new ResponseEntity<>(new ApiResponse()
                .code(ApiResponseConstants.SUCCESS)
                .message(ApiResponseMessage.getMessage(ApiResponseConstants.SUCCESS))
                .description(virtualEnvVarsAdded)
                .name(About.getAppName())
                .version(About.getVersion())
                .timestamp(LocalDateTime.now().format(DateTimeConstants.PATTERN))
                .path(clientRequest.getRequestUri()), HttpStatus.OK);
    }
}
