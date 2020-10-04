package com.github.dinuta.estuary.agent.api;

import com.github.dinuta.estuary.agent.model.api.ApiResponse;
import com.github.dinuta.estuary.agent.model.api.ApiResponseCommandDescription;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;
import java.io.IOException;

@Api(value = "command", description = "the command API")
@RequestMapping(value = "")
public interface CommandParallelApi {

    @ApiOperation(value = "Starts multiple commands in blocking mode parallel. Set the client timeout at needed value.", nickname = "commandPost", notes = "", response = ApiResponse.class, tags = {"estuary-agent",})
    @ApiResponses(value = {
            @io.swagger.annotations.ApiResponse(code = 200, message = "commands start success", response = ApiResponse.class),
            @io.swagger.annotations.ApiResponse(code = 404, message = "commands start failure", response = ApiResponse.class)})
    @RequestMapping(value = "/commandparallel",
            produces = {"application/json"},
            consumes = {"text/plain", "application/json", "application/x-www-form-urlencoded"},
            method = RequestMethod.POST)
    default ResponseEntity<ApiResponseCommandDescription> commandPost(@ApiParam(value = "Commands to run. E.g. ls -lrt", required = true) @Valid @RequestBody String commands, @ApiParam(value = "") @RequestHeader(value = "Token", required = false) String token) throws IOException {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

}
