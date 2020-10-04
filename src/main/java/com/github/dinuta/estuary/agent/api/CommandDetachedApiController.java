package com.github.dinuta.estuary.agent.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dinuta.estuary.agent.component.ClientRequest;
import com.github.dinuta.estuary.agent.component.CommandRunner;
import com.github.dinuta.estuary.agent.constants.About;
import com.github.dinuta.estuary.agent.constants.ApiResponseConstants;
import com.github.dinuta.estuary.agent.constants.ApiResponseMessage;
import com.github.dinuta.estuary.agent.constants.DateTimeConstants;
import com.github.dinuta.estuary.agent.model.api.ApiResponse;
import com.github.dinuta.estuary.agent.model.api.CommandDescription;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
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
    public CommandDetachedApiController(ObjectMapper objectMapper, HttpServletRequest request) {
        this.objectMapper = objectMapper;
        this.request = request;
    }

    public ResponseEntity<ApiResponse> commandDetachedDelete(@ApiParam(value = "") @RequestHeader(value = "Token", required = false) String token) {
        String accept = request.getHeader("Accept");
        return new ResponseEntity<>(new ApiResponse()
                .code(ApiResponseConstants.NOT_IMPLEMENTED)
                .message(ApiResponseMessage.getMessage(ApiResponseConstants.NOT_IMPLEMENTED))
                .description(ApiResponseMessage.getMessage(ApiResponseConstants.NOT_IMPLEMENTED))
                .name(About.getAppName())
                .version(About.getVersion())
                .timestamp(LocalDateTime.now().format(DateTimeConstants.PATTERN))
                .path(clientRequest.getRequestUri()), HttpStatus.NOT_IMPLEMENTED);
    }

    public ResponseEntity<ApiResponse> commandDetachedGet(@ApiParam(value = "") @RequestHeader(value = "Token", required = false) String token) {
        String accept = request.getHeader("Accept");
        String testInfoName = "command_detached_info.json";
        String testInfoFilename = new File(".").getAbsolutePath() + "/" + testInfoName;
        log.debug(testInfoName + " Path: " + testInfoFilename);

        File testInfo = new File(testInfoFilename);
        CommandDescription commandDescription = new CommandDescription();
        try {
            if (!testInfo.exists())
                writeContentInFile(testInfo, commandDescription);
            Path path = Paths.get(testInfoFilename);
            String fileContent = String.join("\n", Files.readAllLines(path));
            commandDescription = objectMapper.readValue(fileContent, CommandDescription.class);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse()
                    .code(ApiResponseConstants.GET_TEST_INFO_FAILURE)
                    .message(ApiResponseMessage.getMessage(ApiResponseConstants.GET_TEST_INFO_FAILURE))
                    .description(ExceptionUtils.getStackTrace(e))
                    .name(About.getAppName())
                    .version(About.getVersion())
                    .timestamp(LocalDateTime.now().format(DateTimeConstants.PATTERN))
                    .path(clientRequest.getRequestUri()), HttpStatus.OK);
        }

        return new ResponseEntity<>(new ApiResponse()
                .code(ApiResponseConstants.SUCCESS)
                .message(ApiResponseMessage.getMessage(ApiResponseConstants.SUCCESS))
                .description(commandDescription)
                .name(About.getAppName())
                .version(About.getVersion())
                .timestamp(LocalDateTime.now().format(DateTimeConstants.PATTERN))
                .path(clientRequest.getRequestUri()), HttpStatus.OK);
    }

    public ResponseEntity<ApiResponse> commandDetachedIdPost(@ApiParam(value = "Command detached id set by the user", required = true) @PathVariable("id") String id, @ApiParam(value = "List of commands to run one after the other. E.g. make/mvn/sh/npm", required = true) @Valid @RequestBody String commandContent, @ApiParam(value = "") @RequestHeader(value = "Token", required = false) String token) {
        String accept = request.getHeader("Accept");
        String testInfoFilename = new File(".").getAbsolutePath() + "/command_detached_info.json";
        File testInfo = new File(testInfoFilename);
        CommandDescription commandDescription = new CommandDescription()
                .started(true)
                .finished(false)
                .id(id);

        if (commandContent == null) {
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
            writeContentInFile(testInfo, commandDescription);
            String commandsStripped = commandContent.replace("\r\n", "\n").stripLeading().stripTrailing();
            List<String> commandsList = Arrays.asList(commandsStripped.split("\n"))
                    .stream().map(elem -> elem.stripLeading().stripTrailing()).collect(Collectors.toList());
            log.debug("Executing commands: " + commandsList.toString());

            List<String> startPyArgumentsList = new ArrayList<>();
            startPyArgumentsList.add(id);
            startPyArgumentsList.add(String.join(";", commandsList.toArray(new String[0])));

            log.debug("Sending args: " + startPyArgumentsList.toString());
            commandRunner.runStartCommandDetached(startPyArgumentsList);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse()
                    .code(ApiResponseConstants.COMMAND_DETACHED_START_FAILURE)
                    .message(String.format(ApiResponseMessage.getMessage(ApiResponseConstants.COMMAND_DETACHED_START_FAILURE)))
                    .description(ExceptionUtils.getStackTrace(e))
                    .name(About.getAppName())
                    .version(About.getVersion())
                    .timestamp(LocalDateTime.now().format(DateTimeConstants.PATTERN))
                    .path(clientRequest.getRequestUri()), HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(new ApiResponse()
                .code(ApiResponseConstants.SUCCESS)
                .message(String.format(ApiResponseMessage.getMessage(ApiResponseConstants.SUCCESS)))
                .description(id)
                .name(About.getAppName())
                .version(About.getVersion())
                .timestamp(LocalDateTime.now().format(DateTimeConstants.PATTERN))
                .path(clientRequest.getRequestUri()), HttpStatus.OK);
    }

    private void writeContentInFile(File testInfo, CommandDescription commandDescription) throws IOException {
        FileWriter fileWriter = new FileWriter(testInfo);
        fileWriter.write(objectMapper.writeValueAsString(commandDescription));
        fileWriter.flush();
        fileWriter.close();
    }
}
