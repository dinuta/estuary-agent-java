package com.github.dinuta.estuary.agent.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dinuta.estuary.agent.component.About;
import com.github.dinuta.estuary.agent.component.ClientRequest;
import com.github.dinuta.estuary.agent.component.CommandRunner;
import com.github.dinuta.estuary.agent.component.ProcessHolder;
import com.github.dinuta.estuary.agent.constants.ApiResponseCode;
import com.github.dinuta.estuary.agent.constants.ApiResponseMessage;
import com.github.dinuta.estuary.agent.constants.DateTimeConstants;
import com.github.dinuta.estuary.agent.exception.ApiException;
import com.github.dinuta.estuary.agent.model.ProcessState;
import com.github.dinuta.estuary.agent.model.api.ApiResponse;
import com.github.dinuta.estuary.agent.model.api.CommandDescription;
import com.github.dinuta.estuary.agent.utils.ProcessUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Api(tags = {"estuary-agent"})
@Controller
public class CommandApiController implements CommandApi {

    private static final Logger log = LoggerFactory.getLogger(CommandApiController.class);

    private final ObjectMapper objectMapper;

    private final HttpServletRequest request;

    @Autowired
    private CommandRunner commandRunner;

    @Autowired
    private ClientRequest clientRequest;

    @Autowired
    private ProcessHolder processHolder;

    @Autowired
    private About about;

    @Autowired
    public CommandApiController(ObjectMapper objectMapper, HttpServletRequest request) {
        this.objectMapper = objectMapper;
        this.request = request;
    }

    public ResponseEntity<ApiResponse> commandGetAll() {
        String accept = request.getHeader("Accept");

        log.debug("Dumping all in memory processes associated with active commands");
        return new ResponseEntity<>(ApiResponse.builder()
                .code(ApiResponseCode.SUCCESS.getCode())
                .message(ApiResponseMessage.getMessage(ApiResponseCode.SUCCESS.getCode()))
                .description(processHolder.dumpAll())
                .name(about.getAppName())
                .version(about.getVersion())
                .timestamp(LocalDateTime.now().format(DateTimeConstants.PATTERN))
                .path(clientRequest.getRequestUri())
                .build(), HttpStatus.OK);
    }


    public ResponseEntity<ApiResponse> commandDeleteAll() {
        String accept = request.getHeader("Accept");
        log.debug("Killing all processes associated with active commands");
        log.debug(String.format("Active processes number: %s", processHolder.getAll().size()));

        for (Map.Entry<ProcessState, String> entry : processHolder.getAll().entrySet()) {
            ProcessState processState = entry.getKey();
            if (processState.getProcess().isAlive()) {
                try {
                    ProcessUtils.killProcessAndChildren(processState);
                } catch (Exception e) {
                    throw new ApiException(ApiResponseCode.COMMAND_STOP_FAILURE.getCode(),
                            ApiResponseMessage.getMessage(ApiResponseCode.COMMAND_STOP_FAILURE.getCode()));
                }
            }
        }

        processHolder.clearAll();

        log.debug(String.format("Active processes number: %s", processHolder.getAll().size()));
        return new ResponseEntity<>(ApiResponse.builder()
                .code(ApiResponseCode.SUCCESS.getCode())
                .message(ApiResponseMessage.getMessage(ApiResponseCode.SUCCESS.getCode()))
                .description(processHolder.dumpAll())
                .name(about.getAppName())
                .version(about.getVersion())
                .timestamp(LocalDateTime.now().format(DateTimeConstants.PATTERN))
                .path(clientRequest.getRequestUri())
                .build(), HttpStatus.OK);
    }

    public ResponseEntity<ApiResponse> commandPost(@ApiParam(value = "Commands to run. E.g. ls -lrt", required = true) @Valid @RequestBody String commands) {
        String accept = request.getHeader("Accept");
        String commandsStripped = commands.replace("\r\n", "\n").stripLeading().stripTrailing();
        List<String> commandsList = Arrays.asList(commandsStripped.split("\n"))
                .stream().map(elem -> elem.stripLeading().stripTrailing()).collect(Collectors.toList());

        log.debug("Executing commands: " + commandsList.toString());
        CommandDescription commandDescription;
        try {
            commandDescription = commandRunner.runCommands(commandsList.toArray(new String[0]));
        } catch (IOException e) {
            throw new ApiException(ApiResponseCode.COMMAND_EXEC_FAILURE.getCode(),
                    ApiResponseMessage.getMessage(ApiResponseCode.COMMAND_EXEC_FAILURE.getCode()));
        }
        return new ResponseEntity<>(ApiResponse.builder()
                .code(ApiResponseCode.SUCCESS.getCode())
                .message(ApiResponseMessage.getMessage(ApiResponseCode.SUCCESS.getCode()))
                .description(commandDescription)
                .name(about.getAppName())
                .version(about.getVersion())
                .timestamp(LocalDateTime.now().format(DateTimeConstants.PATTERN))
                .path(clientRequest.getRequestUri())
                .build(), HttpStatus.OK);
    }
}
