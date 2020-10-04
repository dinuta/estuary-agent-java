package com.github.dinuta.estuary.agent.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dinuta.estuary.agent.component.ClientRequest;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;

@Api(tags = {"estuary-agent"})
@Controller
public class FileApiController implements FileApi {

    private static final Logger log = LoggerFactory.getLogger(FileApiController.class);

    private final ObjectMapper objectMapper;

    private final HttpServletRequest request;

    @Autowired
    private ClientRequest clientRequest;

    @Autowired
    public FileApiController(ObjectMapper objectMapper, HttpServletRequest request) {
        this.objectMapper = objectMapper;
        this.request = request;
    }

    public ResponseEntity<? extends Object> fileGet(@ApiParam(value = "") @RequestHeader(value = "Token", required = false) String token, @ApiParam(value = "Target file path to get") @RequestHeader(value = "File-Path", required = false) String filePath) {
        String accept = request.getHeader("Accept");
        String headerName = "File-Path";
        List<String> fileContent;

        log.debug(headerName + " Header: " + filePath);
        if (filePath == null) {
            return new ResponseEntity<>(new ApiResponse()
                    .code(ApiResponseConstants.HTTP_HEADER_NOT_PROVIDED)
                    .message(String.format(ApiResponseMessage.getMessage(ApiResponseConstants.HTTP_HEADER_NOT_PROVIDED), headerName))
                    .description(String.format(ApiResponseMessage.getMessage(ApiResponseConstants.HTTP_HEADER_NOT_PROVIDED), headerName))
                    .name(About.getAppName())
                    .version(About.getVersion())
                    .timestamp(LocalDateTime.now().format(DateTimeConstants.PATTERN))
                    .path(clientRequest.getRequestUri()), HttpStatus.NOT_FOUND);
        }

        try {
            Path path = Paths.get(filePath);
            fileContent = Files.readAllLines(path);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse()
                    .code(ApiResponseConstants.GET_FILE_FAILURE)
                    .message(ApiResponseMessage.getMessage(ApiResponseConstants.GET_FILE_FAILURE))
                    .description(ExceptionUtils.getStackTrace(e))
                    .name(About.getAppName())
                    .version(About.getVersion())
                    .timestamp(LocalDateTime.now().format(DateTimeConstants.PATTERN))
                    .path(clientRequest.getRequestUri()), HttpStatus.NOT_FOUND);
        }

        return ResponseEntity.ok()
                .contentType(MediaType.valueOf("text/plain"))
                .body(String.join("\n", fileContent));
    }

    public ResponseEntity<ApiResponse> filePut(@ApiParam(value = "The content of the file", required = true) @Valid @RequestBody byte[] content, @ApiParam(value = "", required = true) @RequestHeader(value = "File-Path", required = false) String filePath, @ApiParam(value = "") @RequestHeader(value = "Token", required = false) String token) {
        String accept = request.getHeader("Accept");
        String headerName = "File-Path";

        log.debug(headerName + " Header: " + filePath);
        if (filePath == null) {
            return new ResponseEntity<>(new ApiResponse()
                    .code(ApiResponseConstants.HTTP_HEADER_NOT_PROVIDED)
                    .message(String.format(ApiResponseMessage.getMessage(ApiResponseConstants.HTTP_HEADER_NOT_PROVIDED), headerName))
                    .description(String.format(ApiResponseMessage.getMessage(ApiResponseConstants.HTTP_HEADER_NOT_PROVIDED), headerName))
                    .name(About.getAppName())
                    .version(About.getVersion())
                    .timestamp(LocalDateTime.now().format(DateTimeConstants.PATTERN))
                    .path(clientRequest.getRequestUri()), HttpStatus.NOT_FOUND);
        }

        if (content == null) {
            return new ResponseEntity<>(new ApiResponse()
                    .code(ApiResponseConstants.EMPTY_REQUEST_BODY_PROVIDED)
                    .message(String.format(ApiResponseMessage.getMessage(ApiResponseConstants.EMPTY_REQUEST_BODY_PROVIDED)))
                    .description(String.format(ApiResponseMessage.getMessage(ApiResponseConstants.EMPTY_REQUEST_BODY_PROVIDED)))
                    .name(About.getAppName())
                    .version(About.getVersion())
                    .timestamp(LocalDateTime.now().format(DateTimeConstants.PATTERN))
                    .path(clientRequest.getRequestUri()), HttpStatus.NOT_FOUND);
        }

        try {
            Path path = Paths.get(filePath);
            Files.write(path, content);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse()
                    .code(ApiResponseConstants.UPLOAD_FILE_FAILURE)
                    .message(ApiResponseMessage.getMessage(ApiResponseConstants.UPLOAD_FILE_FAILURE))
                    .description(ExceptionUtils.getStackTrace(e))
                    .name(About.getAppName())
                    .version(About.getVersion())
                    .timestamp(LocalDateTime.now().format(DateTimeConstants.PATTERN))
                    .path(clientRequest.getRequestUri()), HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(new ApiResponse()
                .code(ApiResponseConstants.SUCCESS)
                .message(ApiResponseMessage.getMessage(ApiResponseConstants.SUCCESS))
                .description(ApiResponseMessage.getMessage(ApiResponseConstants.SUCCESS))
                .name(About.getAppName())
                .version(About.getVersion())
                .timestamp(LocalDateTime.now().format(DateTimeConstants.PATTERN))
                .path(clientRequest.getRequestUri()), HttpStatus.OK);
    }

}
