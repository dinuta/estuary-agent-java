package com.github.dinuta.estuary.agent.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dinuta.estuary.agent.component.About;
import com.github.dinuta.estuary.agent.component.ClientRequest;
import com.github.dinuta.estuary.agent.component.CommandRunner;
import com.github.dinuta.estuary.agent.constants.ApiResponseCode;
import com.github.dinuta.estuary.agent.constants.ApiResponseMessage;
import com.github.dinuta.estuary.agent.constants.DateTimeConstants;
import com.github.dinuta.estuary.agent.exception.ApiException;
import com.github.dinuta.estuary.agent.model.StateHolder;
import com.github.dinuta.estuary.agent.model.api.ApiResponse;
import com.github.dinuta.estuary.agent.model.api.CommandDescription;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import lombok.Cleanup;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

@Api(tags = {"estuary-agent"})
@Controller
public class CommandDetachedApiController implements CommandDetachedApi {

    private static final Logger log = LoggerFactory.getLogger(CommandDetachedApiController.class);

    private final ObjectMapper objectMapper;

    private final HttpServletRequest request;

    @Autowired
    private CommandRunner commandRunner;

    @Autowired
    private ClientRequest clientRequest;

    @Autowired
    private About about;

    @Autowired
    private StateHolder stateHolder;

    @Autowired
    public CommandDetachedApiController(ObjectMapper objectMapper, HttpServletRequest request) {
        this.objectMapper = objectMapper;
        this.request = request;
    }

    public ResponseEntity<ApiResponse> commandDetachedDelete() {
        String accept = request.getHeader("Accept");
        return new ResponseEntity<>(ApiResponse.builder()
                .code(ApiResponseCode.NOT_IMPLEMENTED.getCode())
                .message(ApiResponseMessage.getMessage(ApiResponseCode.NOT_IMPLEMENTED.getCode()))
                .description(ApiResponseMessage.getMessage(ApiResponseCode.NOT_IMPLEMENTED.getCode()))
                .name(about.getAppName())
                .version(about.getVersion())
                .timestamp(LocalDateTime.now().format(DateTimeConstants.PATTERN))
                .path(clientRequest.getRequestUri())
                .build(), HttpStatus.NOT_IMPLEMENTED);
    }

    public ResponseEntity<ApiResponse> commandDetachedGet() {
        String accept = request.getHeader("Accept");
        String testInfoFilename = stateHolder.getLastCommand();
        log.debug("Reading from path: " + testInfoFilename);

        File testInfo = new File(testInfoFilename);
        CommandDescription commandDescription = new CommandDescription();
        try {
            if (!testInfo.exists())
                writeContentInFile(testInfo, commandDescription);
        } catch (IOException e) {
            throw new ApiException(ApiResponseCode.GET_COMMAND_INFO_FAILURE.getCode(),
                    ApiResponseMessage.getMessage(ApiResponseCode.GET_COMMAND_INFO_FAILURE.getCode()));
        }

        try (InputStream inputStream = new FileInputStream(testInfo)) {
            String fileContent = IOUtils.toString(inputStream, "UTF-8");
            commandDescription = objectMapper.readValue(fileContent, CommandDescription.class);
        } catch (IOException e) {
            throw new ApiException(ApiResponseCode.GET_COMMAND_INFO_FAILURE.getCode(),
                    ApiResponseMessage.getMessage(ApiResponseCode.GET_COMMAND_INFO_FAILURE.getCode()));
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

    public ResponseEntity<ApiResponse> commandDetachedIdGet(@ApiParam(value = "Command detached id set by the user", required = true) @PathVariable("id") String id) {
        String accept = request.getHeader("Accept");
        String testInfoFilename = String.format(stateHolder.getLastCommandFormat(), id);
        log.debug("Reading content from file: " + testInfoFilename);

        CommandDescription commandDescription;
        try (InputStream is = new FileInputStream(testInfoFilename)) {
            String fileContent = IOUtils.toString(is, "UTF-8");
            commandDescription = objectMapper.readValue(fileContent, CommandDescription.class);
        } catch (IOException e) {
            throw new ApiException(ApiResponseCode.GET_COMMAND_INFO_FAILURE.getCode(),
                    ApiResponseMessage.getMessage(ApiResponseCode.GET_COMMAND_INFO_FAILURE.getCode()));
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

    public ResponseEntity<ApiResponse> commandDetachedIdPost(@ApiParam(value = "Command detached id set by the user", required = true) @PathVariable("id") String id, @ApiParam(value = "List of commands to run one after the other. E.g. make/mvn/sh/npm", required = true) @Valid @RequestBody String commandContent) {
        String accept = request.getHeader("Accept");

        File testInfo = new File(String.format(stateHolder.getLastCommandFormat(), id));
        CommandDescription commandDescription = CommandDescription.builder()
                .started(true)
                .finished(false)
                .id(id)
                .commands(new LinkedHashMap<>())
                .build();

        if (commandContent == null) {
            throw new ApiException(ApiResponseCode.EMPTY_REQUEST_BODY_PROVIDED.getCode(),
                    ApiResponseMessage.getMessage(ApiResponseCode.EMPTY_REQUEST_BODY_PROVIDED.getCode()));
        }

        try {
            writeContentInFile(testInfo, commandDescription);
            String commandsStripped = commandContent.replace("\r\n", "\n").stripLeading().stripTrailing();
            List<String> commandsList = Arrays.asList(commandsStripped.split("\n"))
                    .stream().map(elem -> elem.stripLeading().stripTrailing()).collect(Collectors.toList());
            log.debug("Executing commands: " + commandsList.toString());

            List<String> argumentsList = new ArrayList<>();
            argumentsList.add("--cid=" + id);
            argumentsList.add("--enableStreams=false");
            argumentsList.add("--args=\"" + String.join(";;", commandsList.toArray(new String[0])) + "\"");

            log.debug("Sending args: " + argumentsList.toString());
            commandRunner.runStartCommandInBackground(argumentsList);
            stateHolder.setLastCommand(id);
        } catch (Exception e) {
            throw new ApiException(ApiResponseCode.COMMAND_START_FAILURE.getCode(),
                    String.format(ApiResponseMessage.getMessage(ApiResponseCode.COMMAND_START_FAILURE.getCode()), id));
        }

        return new ResponseEntity<>(ApiResponse.builder()
                .code(ApiResponseCode.SUCCESS.getCode())
                .message(String.format(ApiResponseMessage.getMessage(ApiResponseCode.SUCCESS.getCode())))
                .description(id)
                .name(about.getAppName())
                .version(about.getVersion())
                .timestamp(LocalDateTime.now().format(DateTimeConstants.PATTERN))
                .path(clientRequest.getRequestUri())
                .build(), HttpStatus.ACCEPTED);
    }

    private void writeContentInFile(File testInfo, CommandDescription commandDescription) throws IOException {
        @Cleanup FileWriter fileWriter = new FileWriter(testInfo);
        fileWriter.write(objectMapper.writeValueAsString(commandDescription));
        fileWriter.flush();
    }
}
