package com.github.dinuta.estuary.agent.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dinuta.estuary.agent.component.ClientRequest;
import com.github.dinuta.estuary.agent.component.CommandRunner;
import com.github.dinuta.estuary.agent.constants.About;
import com.github.dinuta.estuary.agent.constants.ApiResponseCode;
import com.github.dinuta.estuary.agent.constants.ApiResponseMessage;
import com.github.dinuta.estuary.agent.constants.DateTimeConstants;
import com.github.dinuta.estuary.agent.model.api.ApiResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Api(tags = {"estuary-agent"})
@Controller
public class CommandParallelApiController implements CommandParallelApi {

    private static final Logger log = LoggerFactory.getLogger(CommandParallelApiController.class);

    private final ObjectMapper objectMapper;

    private final HttpServletRequest request;

    @Autowired
    private CommandRunner commandRunner;

    @Autowired
    private ClientRequest clientRequest;

    @Autowired
    public CommandParallelApiController(ObjectMapper objectMapper, HttpServletRequest request) {
        this.objectMapper = objectMapper;
        this.request = request;
    }

    public ResponseEntity<ApiResponse> commandPost(@ApiParam(value = "Commands to run. E.g. ls -lrt", required = true) @Valid @RequestBody String commands, @ApiParam(value = "") @RequestHeader(value = "Token", required = false) String token) throws IOException {
        String commandsStripped = commands.replace("\r\n", "\n").stripLeading().stripTrailing();
        List<String> commandsList = Arrays.asList(commandsStripped.split("\n"))
                .stream().map(elem -> elem.stripLeading().stripTrailing()).collect(Collectors.toList());

        log.debug("Executing commands: " + commandsList.toString());
        return new ResponseEntity<>(ApiResponse.builder()
                .code(ApiResponseCode.SUCCESS.getCode())
                .message(ApiResponseMessage.getMessage(ApiResponseCode.SUCCESS.getCode()))
                .description(commandRunner.runCommandsParallel(commandsList.toArray(new String[0])))
                .name(About.getAppName())
                .version(About.getVersion())
                .timestamp(LocalDateTime.now().format(DateTimeConstants.PATTERN))
                .path(clientRequest.getRequestUri())
                .build(), HttpStatus.OK);
    }
}
