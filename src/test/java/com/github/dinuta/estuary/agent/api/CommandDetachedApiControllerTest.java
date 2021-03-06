package com.github.dinuta.estuary.agent.api;

import com.github.dinuta.estuary.agent.api.utils.HttpRequestUtils;
import com.github.dinuta.estuary.agent.component.About;
import com.github.dinuta.estuary.agent.component.Authentication;
import com.github.dinuta.estuary.agent.constants.ApiResponseCode;
import com.github.dinuta.estuary.agent.constants.ApiResponseMessage;
import com.github.dinuta.estuary.agent.constants.DateTimeConstants;
import com.github.dinuta.estuary.agent.model.api.ApiResponse;
import com.github.dinuta.estuary.agent.model.api.CommandDescription;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import static com.github.dinuta.estuary.agent.constants.DateTimeConstants.PATTERN;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
public class CommandDetachedApiControllerTest {
    private static final Logger log = LoggerFactory.getLogger(CommandDetachedApiControllerTest.class);

    private final static String SERVER_PREFIX = "http://localhost:";

    @LocalServerPort
    private int port;

    @Autowired
    private HttpRequestUtils httpRequestUtils;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private About about;

    @Autowired
    private Authentication auth;

    @ParameterizedTest
    @ValueSource(
            strings = {
                    "ls -lrt | grep README.md;README.md"
            }
    )
    public void whenSendingCommandsWithSpacesNothingIsBrokenAndThenApiReturnsZeroExitCode(String commandInfo) throws InterruptedException {
        String id = "myId1";
        String expected = commandInfo.split(";")[1];

        ResponseEntity<ApiResponse> responseEntity = postApiResponseCommandDescriptionResponseEntity(commandInfo.split(";")[0], id);
        ApiResponse body = responseEntity.getBody();
        log.info(body.toString());
        assertThat(responseEntity.getStatusCode().value()).isEqualTo(HttpStatus.ACCEPTED.value());
        assertThat(body.getCode()).isEqualTo(ApiResponseCode.SUCCESS.getCode());
        assertThat(body.getMessage()).isEqualTo(
                String.format(ApiResponseMessage.getMessage(ApiResponseCode.SUCCESS.getCode())));
        assertThat(body.getName()).isEqualTo(about.getAppName());
        assertThat(body.getDescription()).isEqualTo(id);
        assertThat(body.getVersion()).isEqualTo(about.getVersion());
        assertThat(LocalDateTime.parse(body.getTimestamp(), PATTERN)).isBefore(LocalDateTime.now());

        Thread.sleep(1000);
        ApiResponse<CommandDescription> body1 = getApiResponseCommandDescriptionResponseEntity().getBody();

        assertThat(body1.getDescription().getCommands().get(commandInfo.split(";")[0]).getDetails().getCode()).isEqualTo(0);
        assertThat(body1.getDescription().getCommands().get(commandInfo.split(";")[0]).getDetails().getErr()).isEqualTo("");
        assertThat(body1.getDescription().getCommands().get(commandInfo.split(";")[0]).getDetails().getOut()).contains(expected);
    }

    @Test
    public void whenSendingTwoCommandsThenApiReturnsSumOfTimeExecutionInSeconds() {
        String testId = "myId2";
        int sleep1 = 2;
        int sleep2 = 3;
        String command1 = "sleep " + sleep1;
//        String command1 = "ping -n " + sleep1 + " 127.0.0.1";
        String command2 = "sleep " + sleep2;
//        String command2 = "ping -n " + sleep2 + " 127.0.0.1";
        String command = command1 + "\n" + command2;
        Map<String, String> headers = new HashMap<>();

        ResponseEntity<ApiResponse<String>> responseEntity = this.restTemplate.withBasicAuth(auth.getUser(), auth.getPassword())
                .exchange(SERVER_PREFIX + port + "/commanddetached/" + testId,
                        HttpMethod.POST,
                        httpRequestUtils.getRequestEntityJsonContentTypeAppText(command, headers),
                        new ParameterizedTypeReference<ApiResponse<String>>() {
                        });


        ApiResponse<String> body = responseEntity.getBody();
        log.info(body.toString());
        assertThat(responseEntity.getStatusCode().value()).isEqualTo(HttpStatus.ACCEPTED.value());
        assertThat(body.getCode()).isEqualTo(ApiResponseCode.SUCCESS.getCode());
        assertThat(body.getMessage()).isEqualTo(
                String.format(ApiResponseMessage.getMessage(ApiResponseCode.SUCCESS.getCode())));
        assertThat(body.getDescription()).isEqualTo(testId);


        await().atMost(sleep1 + 1, SECONDS).until(isCommandFinished(command1));
        ApiResponse<CommandDescription> body1 =
                getApiResponseCommandDescriptionResponseEntity().getBody();

        assertThat(LocalDateTime.parse(body1.getDescription().getFinishedat(), DateTimeConstants.PATTERN)).isBefore(LocalDateTime.now());
        assertThat(LocalDateTime.parse(body1.getDescription().getStartedat(), DateTimeConstants.PATTERN)).isBefore(LocalDateTime.now());
        assertThat(body1.getDescription().isStarted()).isEqualTo(true);
        assertThat(body1.getDescription().isFinished()).isEqualTo(false);
        assertThat(body1.getDescription().getCommands().get(command1).getStatus()).isEqualTo("finished");
        assertThat(body1.getDescription().getCommands().get(command2).getStatus()).isEqualTo("in progress");

        await().atMost(sleep2 + 1, SECONDS).until(isCommandFinished(command2));
        body1 = getApiResponseCommandDescriptionResponseEntity().getBody();

        assertThat(Math.round(body1.getDescription().getDuration())).isEqualTo(Math.round(sleep1 + sleep2));
        assertThat(LocalDateTime.parse(body1.getDescription().getFinishedat(), DateTimeConstants.PATTERN)).isBefore(LocalDateTime.now());
        assertThat(LocalDateTime.parse(body1.getDescription().getStartedat(), DateTimeConstants.PATTERN)).isBefore(LocalDateTime.now());
        assertThat(body1.getDescription().isStarted()).isEqualTo(false);
        assertThat(body1.getDescription().isFinished()).isEqualTo(true);
        assertThat(body1.getDescription().getId()).isEqualTo(testId);
        assertThat(body1.getDescription().getPid()).isGreaterThanOrEqualTo(0);
        assertThat(Math.round(body1.getDescription().getCommands().get(command1).getDuration())).isEqualTo(Math.round(sleep1));
        assertThat(body1.getDescription().getCommands().get(command1).getStatus()).isEqualTo("finished");
        assertThat(Math.round(body1.getDescription().getCommands().get(command2).getDuration())).isEqualTo(Math.round(sleep2));
        assertThat(body1.getDescription().getCommands().get(command2).getStatus()).isEqualTo("finished");
        assertThat(body1.getName()).isEqualTo(about.getAppName());
        assertThat(body1.getPath()).isEqualTo("/commanddetached?");
        assertThat(body1.getVersion()).isEqualTo(about.getVersion());
        assertThat(LocalDateTime.parse(body1.getTimestamp(), PATTERN)).isBefore(LocalDateTime.now());
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                    "ls -lrt | grep README.md;README.md"
            }
    )
    public void whenAskingForExistingDetachedCommandIdThenItIsFound(String commandInfo) throws InterruptedException {
        String id = "myId10";
        String expected = commandInfo.split(";")[1];

        ResponseEntity<ApiResponse<String>> responseEntity = postApiResponseCommandDescriptionEntity(commandInfo.split(";")[0], id);
        ApiResponse body = responseEntity.getBody();

        assertThat(responseEntity.getStatusCode().value()).isEqualTo(HttpStatus.ACCEPTED.value());
        assertThat(body.getCode()).isEqualTo(ApiResponseCode.SUCCESS.getCode());
        assertThat(body.getMessage()).isEqualTo(
                String.format(ApiResponseMessage.getMessage(ApiResponseCode.SUCCESS.getCode())));
        assertThat(body.getName()).isEqualTo(about.getAppName());
        assertThat(body.getDescription()).isEqualTo(id);
        assertThat(body.getVersion()).isEqualTo(about.getVersion());
        assertThat(LocalDateTime.parse(body.getTimestamp(), PATTERN)).isBefore(LocalDateTime.now());

        Thread.sleep(1000);
        ApiResponse<CommandDescription> body1 = getApiResponseCommandDescriptionEntityForId(id).getBody();

        assertThat(body1.getDescription().getId()).isEqualTo(id);
        assertThat(body1.getDescription().getCommands().get(commandInfo.split(";")[0]).getDetails().getCode()).isEqualTo(0);
        assertThat(body1.getDescription().getCommands().get(commandInfo.split(";")[0]).getDetails().getErr()).isEqualTo("");
        assertThat(body1.getDescription().getCommands().get(commandInfo.split(";")[0]).getDetails().getOut()).contains(expected);
    }


    private ResponseEntity<ApiResponse<CommandDescription>> getApiResponseCommandDescriptionEntityForId(String id) {
        Map<String, String> headers = new HashMap<>();
        headers.put(CONTENT_TYPE, MediaType.TEXT_PLAIN.toString());
        return this.restTemplate.withBasicAuth(auth.getUser(), auth.getPassword())
                .exchange(SERVER_PREFIX + port + "/commanddetached/" + id,
                        HttpMethod.GET,
                        httpRequestUtils.getRequestEntityContentTypeAppJson(null, headers),
                        new ParameterizedTypeReference<ApiResponse<CommandDescription>>() {
                        });
    }

    private ResponseEntity<ApiResponse<String>> postApiResponseCommandDescriptionEntity(String command, String id) {
        Map<String, String> headers = new HashMap<>();

        return this.restTemplate.withBasicAuth(auth.getUser(), auth.getPassword())
                .exchange(SERVER_PREFIX + port + "/commanddetached/" + id,
                        HttpMethod.POST,
                        httpRequestUtils.getRequestEntityContentTypeAppJson(command, headers),
                        new ParameterizedTypeReference<ApiResponse<String>>() {
                        });
    }

    private Callable<Boolean> isCommandFinished(String command) {
        return () -> {
            ResponseEntity<ApiResponse<CommandDescription>> responseEntity = getApiResponseCommandDescriptionResponseEntity();
            ApiResponse<CommandDescription> body = responseEntity.getBody();

            if (body.getDescription().getCommands().get(command) == null)
                return Boolean.FALSE;

            return Boolean.valueOf(body.getDescription().getCommands().get(command).getDuration() > 0F);
        };
    }


    private ResponseEntity<ApiResponse<CommandDescription>> getApiResponseCommandDescriptionResponseEntity() {
        Map<String, String> headers = new HashMap<>();
        headers.put(CONTENT_TYPE, MediaType.TEXT_PLAIN.toString());
        return this.restTemplate.withBasicAuth(auth.getUser(), auth.getPassword())
                .exchange(SERVER_PREFIX + port + "/commanddetached",
                        HttpMethod.GET,
                        httpRequestUtils.getRequestEntityContentTypeAppJson(null, headers),
                        new ParameterizedTypeReference<ApiResponse<CommandDescription>>() {
                        });
    }

    private ResponseEntity<ApiResponse> postApiResponseCommandDescriptionResponseEntity(String command, String id) {
        Map<String, String> headers = new HashMap<>();

        return this.restTemplate.withBasicAuth(auth.getUser(), auth.getPassword())
                .exchange(SERVER_PREFIX + port + "/commanddetached/" + id,
                        HttpMethod.POST,
                        httpRequestUtils.getRequestEntityContentTypeAppJson(command, headers),
                        ApiResponse.class);
    }
}
