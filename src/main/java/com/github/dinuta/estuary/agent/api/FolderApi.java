package com.github.dinuta.estuary.agent.api;

import com.github.dinuta.estuary.agent.model.api.ApiResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Api(value = "folder", description = "the folder API")
@RequestMapping(value = "")
public interface FolderApi {

    @ApiOperation(value = "Gets the folder as zip archive. Useful to get test results folder", nickname = "folderGet", notes = "", response = ApiResponse.class, tags = {"estuary-agent",})
    @ApiResponses(value = {
            @io.swagger.annotations.ApiResponse(code = 200, message = "The content of the folder as zip archive", response = ApiResponse.class),
            @io.swagger.annotations.ApiResponse(code = 404, message = "The content of the folder could not be obtained", response = ApiResponse.class)})
    @RequestMapping(value = "/folder",
            produces = {"application/zip", "application/json"},
            method = RequestMethod.GET)
    default ResponseEntity<? extends Object> folderGet(@ApiParam(value = "Target folder path to get as zip", required = false) @RequestHeader(value = "Folder-Path", required = false) String folderPath, @ApiParam(value = "") @RequestHeader(value = "Token", required = false) String token) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

}
