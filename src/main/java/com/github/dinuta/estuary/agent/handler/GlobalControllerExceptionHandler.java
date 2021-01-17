package com.github.dinuta.estuary.agent.handler;


import com.github.dinuta.estuary.agent.component.About;
import com.github.dinuta.estuary.agent.component.ClientRequest;
import com.github.dinuta.estuary.agent.constants.DateTimeConstants;
import com.github.dinuta.estuary.agent.exception.ApiException;
import com.github.dinuta.estuary.agent.model.api.ApiResponse;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalControllerExceptionHandler extends ResponseEntityExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalControllerExceptionHandler.class);

    @Autowired
    MessageSource messageSource;

    @Autowired
    private ClientRequest clientRequest;

    @Autowired
    private About about;

    @ExceptionHandler({ApiException.class})
    public ResponseEntity<ApiResponse> handleException(ApiException e, HttpServletRequest request) {
        log.error("Http error: " + ExceptionUtils.getStackTrace(e));
        return new ResponseEntity<>(ApiResponse.builder()
                .code(e.getCode())
                .message(e.getMessage())
                .description(ExceptionUtils.getStackTrace(e))
                .name(about.getAppName())
                .version(about.getVersion())
                .timestamp(LocalDateTime.now().format(DateTimeConstants.PATTERN))
                .path(clientRequest.getRequestUri())
                .build(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
