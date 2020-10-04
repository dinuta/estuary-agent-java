package com.github.dinuta.estuary.agent.api;

import com.github.dinuta.estuary.agent.model.api.ApiResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Api(value = "commanddetached", description = "the command detached API")
@RequestMapping(value = "")
public interface CommandDetachedApi {

    @ApiOperation(value = "Stops all commands that were previously started in detached mode", nickname = "commandDetachedDelete", notes = "", response = ApiResponse.class, tags = {"estuary-agent",})
    @ApiResponses(value = {
            @io.swagger.annotations.ApiResponse(code = 200, message = "command detached stop success", response = ApiResponse.class),
            @io.swagger.annotations.ApiResponse(code = 404, message = "command detached stop failure", response = ApiResponse.class)})
    @RequestMapping(value = "/commanddetached",
            produces = {"application/json"},
            method = RequestMethod.DELETE)
    default ResponseEntity<ApiResponse> commandDetachedDelete(@ApiParam(value = "") @RequestHeader(value = "Token", required = false) String token) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }


    @ApiOperation(value = "Gets information about the last command started in detached mode", nickname = "commandDetachedGet", notes = "", response = ApiResponse.class, tags = {"estuary-agent",})
    @ApiResponses(value = {
            @io.swagger.annotations.ApiResponse(code = 200, message = "Get command detached info success", response = ApiResponse.class),
            @io.swagger.annotations.ApiResponse(code = 404, message = "Get command detached info failure", response = ApiResponse.class)})
    @RequestMapping(value = "/commanddetached",
            produces = {"application/json"},
            method = RequestMethod.GET)
    default ResponseEntity<ApiResponse> commandDetachedGet(@ApiParam(value = "") @RequestHeader(value = "Token", required = false) String token) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @ApiOperation(value = "Starts the shell commands in detached mode and sequentially", nickname = "commandDetachedIdPost", notes = "", response = ApiResponse.class, tags = {"estuary-agent",})
    @ApiResponses(value = {
            @io.swagger.annotations.ApiResponse(code = 200, message = "commands start success", response = ApiResponse.class),
            @io.swagger.annotations.ApiResponse(code = 404, message = "commands start failure", response = ApiResponse.class)})
    @RequestMapping(value = "/commanddetached/{id}",
            produces = {"application/json"},
            consumes = {"text/plain", "application/json", "application/x-www-form-urlencoded"},
            method = RequestMethod.POST)
    default ResponseEntity<ApiResponse> commandDetachedIdPost(@ApiParam(value = "Command detached id set by the user", required = true) @PathVariable("id") String id, @ApiParam(value = "List of commands to run one after the other. E.g. make/mvn/sh/npm", required = true) @Valid @RequestBody String commandContent, @ApiParam(value = "") @RequestHeader(value = "Token", required = false) String token) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

}
