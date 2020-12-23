package com.github.dinuta.estuary.agent.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dinuta.estuary.agent.component.ClientRequest;
import com.github.dinuta.estuary.agent.constants.About;
import com.github.dinuta.estuary.agent.constants.ApiResponseCode;
import com.github.dinuta.estuary.agent.constants.ApiResponseMessage;
import com.github.dinuta.estuary.agent.constants.DateTimeConstants;
import com.github.dinuta.estuary.agent.exception.ApiException;
import com.github.dinuta.estuary.agent.model.api.ApiResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import org.apache.commons.compress.utils.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.*;
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
            throw new ApiException(ApiResponseCode.HTTP_HEADER_NOT_PROVIDED.code,
                    String.format(ApiResponseMessage.getMessage(ApiResponseCode.HTTP_HEADER_NOT_PROVIDED.code), headerName));
        }
        ByteArrayResource resource;
        try (InputStream inputStream = new FileInputStream(new File(filePath))) {
            resource = new ByteArrayResource(IOUtils.toByteArray(inputStream));
        } catch (IOException e) {
            throw new ApiException(ApiResponseCode.GET_FILE_FAILURE.code,
                    ApiResponseMessage.getMessage(ApiResponseCode.GET_FILE_FAILURE.code));
        }

        return ResponseEntity.ok()
                .body(resource);
    }

    public ResponseEntity<ApiResponse> filePut(@ApiParam(value = "The content of the file", required = true) @Valid @RequestBody byte[] content, @ApiParam(value = "", required = true) @RequestHeader(value = "File-Path", required = false) String filePath, @ApiParam(value = "") @RequestHeader(value = "Token", required = false) String token) {
        String accept = request.getHeader("Accept");
        String headerName = "File-Path";

        log.debug(headerName + " Header: " + filePath);
        if (filePath == null) {
            throw new ApiException(ApiResponseCode.HTTP_HEADER_NOT_PROVIDED.code,
                    String.format(ApiResponseMessage.getMessage(ApiResponseCode.HTTP_HEADER_NOT_PROVIDED.code), headerName));
        }

        if (content == null) {
            throw new ApiException(ApiResponseCode.EMPTY_REQUEST_BODY_PROVIDED.code,
                    ApiResponseMessage.getMessage(ApiResponseCode.EMPTY_REQUEST_BODY_PROVIDED.code));
        }

        try (OutputStream outputStream = new FileOutputStream(new File(filePath))) {
            org.apache.commons.io.IOUtils.write(content, outputStream);
        } catch (IOException e) {
            throw new ApiException(ApiResponseCode.UPLOAD_FILE_FAILURE.code,
                    ApiResponseMessage.getMessage(ApiResponseCode.UPLOAD_FILE_FAILURE.code));
        }

        return new ResponseEntity<>(ApiResponse.builder()
                .code(ApiResponseCode.SUCCESS.code)
                .message(ApiResponseMessage.getMessage(ApiResponseCode.SUCCESS.code))
                .description(ApiResponseMessage.getMessage(ApiResponseCode.SUCCESS.code))
                .name(About.getAppName())
                .version(About.getVersion())
                .timestamp(LocalDateTime.now().format(DateTimeConstants.PATTERN))
                .path(clientRequest.getRequestUri())
                .build(), HttpStatus.OK);
    }

}
