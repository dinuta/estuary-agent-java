package com.github.dinuta.estuary.agent.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dinuta.estuary.agent.component.ClientRequest;
import com.github.dinuta.estuary.agent.constants.ApiResponseCode;
import com.github.dinuta.estuary.agent.constants.ApiResponseMessage;
import com.github.dinuta.estuary.agent.exception.ApiException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import org.apache.commons.compress.utils.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;
import org.zeroturnaround.zip.ZipUtil;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@Api(tags = {"estuary-agent"})
@Controller
public class FolderApiController implements FolderApi {

    private static final Logger log = LoggerFactory.getLogger(FolderApiController.class);

    private final ObjectMapper objectMapper;

    private final HttpServletRequest request;

    @Autowired
    private ClientRequest clientRequest;

    @Autowired
    public FolderApiController(ObjectMapper objectMapper, HttpServletRequest request) {
        this.objectMapper = objectMapper;
        this.request = request;
    }

    public ResponseEntity<? extends Object> folderGet(@ApiParam(value = "Target folder path to get as zip", required = false) @RequestHeader(value = "Folder-Path", required = false) String folderPath, @ApiParam(value = "") @RequestHeader(value = "Token", required = false) String token) {
        String accept = request.getHeader("Accept");
        String archiveNamePath = "results.zip";
        String headerName = "Folder-Path";

        log.debug(headerName + " Header: " + folderPath);
        if (folderPath == null) {
            throw new ApiException(ApiResponseCode.HTTP_HEADER_NOT_PROVIDED.code,
                    String.format(ApiResponseMessage.getMessage(ApiResponseCode.HTTP_HEADER_NOT_PROVIDED.code), headerName));
        }

        File file = new File(archiveNamePath);
        ByteArrayResource resource;
        try (InputStream inputStream = new FileInputStream(archiveNamePath)) {
            ZipUtil.pack(new File(folderPath), file, name -> name);
            resource = new ByteArrayResource(IOUtils.toByteArray(inputStream));
        } catch (IOException e) {
            throw new ApiException(ApiResponseCode.FOLDER_ZIP_FAILURE.code,
                    String.format(ApiResponseMessage.getMessage(ApiResponseCode.FOLDER_ZIP_FAILURE.code), folderPath));
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + file.getName())
                .contentType(MediaType.valueOf("application/zip"))
                .contentLength(file.length())
                .body(resource);
    }

}
