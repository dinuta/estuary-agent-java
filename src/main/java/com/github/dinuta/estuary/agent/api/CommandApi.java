package com.github.dinuta.estuary.agent.api;

import com.github.dinuta.estuary.agent.model.api.ApiResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;

@Api(value = "command")
@RequestMapping(value = "")
public interface CommandApi {

    @ApiOperation(value = "Dumps the active commands that are running on the Agent.", nickname = "commandGetAll", notes = "", response = ApiResponse.class, tags = {"estuary-agent",})
    @ApiResponses(value = {
            @io.swagger.annotations.ApiResponse(code = 200, message = "All commands in memory dump success", response = ApiResponse.class),
            @io.swagger.annotations.ApiResponse(code = 500, message = "All commands in memory dump failure", response = ApiResponse.class)})
    @RequestMapping(value = "/commands",
            produces = {"application/json"},
            method = RequestMethod.GET)
    default ResponseEntity<ApiResponse> commandGetAll() {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @ApiOperation(value = "Stops all the active commands on the Agent by terminating their corresponding process", nickname = "commandDeleteAll", notes = "", response = ApiResponse.class, tags = {"estuary-agent",})
    @ApiResponses(value = {
            @io.swagger.annotations.ApiResponse(code = 200, message = "All commands process terminated success", response = ApiResponse.class),
            @io.swagger.annotations.ApiResponse(code = 500, message = "All commands process terminated failure", response = ApiResponse.class)})
    @RequestMapping(value = "/commands",
            produces = {"application/json"},
            method = RequestMethod.DELETE)
    default ResponseEntity<ApiResponse> commandDeleteAll() {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @ApiOperation(value = "Starts multiple commands in blocking mode sequentially. Set the client timeout at needed value.", nickname = "commandPost", notes = "", response = ApiResponse.class, tags = {"estuary-agent",})
    @ApiResponses(value = {
            @io.swagger.annotations.ApiResponse(code = 200, message = "commands start success", response = ApiResponse.class),
            @io.swagger.annotations.ApiResponse(code = 404, message = "commands start failure", response = ApiResponse.class)})
    @RequestMapping(value = "/command",
            produces = {"application/json"},
            consumes = {"text/plain", "application/json", "application/x-www-form-urlencoded"},
            method = RequestMethod.POST)
    default ResponseEntity<ApiResponse> commandPost(@ApiParam(value = "Commands to run. E.g. ls -lrt", required = true) @Valid @RequestBody String commands) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

}
