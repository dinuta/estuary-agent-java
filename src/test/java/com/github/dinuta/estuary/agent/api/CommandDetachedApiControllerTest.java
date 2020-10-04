package com.github.dinuta.estuary.agent.api;

import com.github.dinuta.estuary.agent.api.utils.HttpRequestUtils;
import com.github.dinuta.estuary.agent.constants.About;
import com.github.dinuta.estuary.agent.constants.ApiResponseConstants;
import com.github.dinuta.estuary.agent.constants.ApiResponseMessage;
import com.github.dinuta.estuary.agent.constants.DateTimeConstants;
import com.github.dinuta.estuary.agent.model.api.ApiResponse;
import com.github.dinuta.estuary.agent.model.api.ApiResponseCommandDescription;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
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
    private final static String SERVER_PREFIX = "http://localhost:";

    @LocalServerPort
    private int port;

    @Autowired
    private HttpRequestUtils httpRequestUtils;

    @Autowired
    private TestRestTemplate restTemplate;

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

        assertThat(responseEntity.getStatusCode().value()).isEqualTo(HttpStatus.OK.value());
        assertThat(body.getCode()).isEqualTo(ApiResponseConstants.SUCCESS);
        assertThat(body.getMessage()).isEqualTo(
                String.format(ApiResponseMessage.getMessage(ApiResponseConstants.SUCCESS)));
        assertThat(body.getName()).isEqualTo(About.getAppName());
        assertThat(body.getDescription()).isEqualTo(id);
        assertThat(body.getVersion()).isEqualTo(About.getVersion());
        assertThat(LocalDateTime.parse(body.getTimestamp(), PATTERN)).isBefore(LocalDateTime.now());

        Thread.sleep(1000);
        ApiResponseCommandDescription body1 = getApiResponseCommandDescriptionResponseEntity().getBody();

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

        ResponseEntity<ApiResponse> responseEntity = this.restTemplate
                .exchange(SERVER_PREFIX + port + "/commanddetached/" + testId,
                        HttpMethod.POST,
                        httpRequestUtils.getRequestEntityJsonContentTypeAppText(command, headers),
                        ApiResponse.class);


        ApiResponse body = responseEntity.getBody();

        assertThat(responseEntity.getStatusCode().value()).isEqualTo(HttpStatus.OK.value());
        assertThat(body.getCode()).isEqualTo(ApiResponseConstants.SUCCESS);
        assertThat(body.getMessage()).isEqualTo(
                String.format(ApiResponseMessage.getMessage(ApiResponseConstants.SUCCESS)));
        assertThat(body.getDescription()).isEqualTo(testId);


        await().atMost(sleep1 + 1, SECONDS).until(isCommandFinished(command1));
        ApiResponseCommandDescription body1 =
                getApiResponseCommandDescriptionResponseEntity().getBody();

        assertThat(LocalDateTime.parse(body1.getDescription().getFinishedat(), DateTimeConstants.PATTERN)).isBefore(LocalDateTime.now());
        assertThat(LocalDateTime.parse(body1.getDescription().getStartedat(), DateTimeConstants.PATTERN)).isBefore(LocalDateTime.now());
        assertThat(body1.getDescription().getStarted()).isEqualTo(true);
        assertThat(body1.getDescription().getFinished()).isEqualTo(false);
        assertThat(body1.getDescription().getCommands().get(command1).getStatus()).isEqualTo("finished");
        assertThat(body1.getDescription().getCommands().get(command2).getStatus()).isEqualTo("in progress");

        await().atMost(sleep2 + 1, SECONDS).until(isCommandFinished(command2));
        body1 = getApiResponseCommandDescriptionResponseEntity().getBody();

        assertThat(Math.round(body1.getDescription().getDuration())).isEqualTo(Math.round(sleep1 + sleep2));
        assertThat(LocalDateTime.parse(body1.getDescription().getFinishedat(), DateTimeConstants.PATTERN)).isBefore(LocalDateTime.now());
        assertThat(LocalDateTime.parse(body1.getDescription().getStartedat(), DateTimeConstants.PATTERN)).isBefore(LocalDateTime.now());
        assertThat(body1.getDescription().getStarted()).isEqualTo(false);
        assertThat(body1.getDescription().getFinished()).isEqualTo(true);
        assertThat(body1.getDescription().getId()).isEqualTo(testId);
        assertThat(body1.getDescription().getPid()).isGreaterThanOrEqualTo(0);
        assertThat(Math.round(body1.getDescription().getCommands().get(command1).getDuration())).isEqualTo(Math.round(sleep1));
        assertThat(body1.getDescription().getCommands().get(command1).getStatus()).isEqualTo("finished");
        assertThat(Math.round(body1.getDescription().getCommands().get(command2).getDuration())).isEqualTo(Math.round(sleep2));
        assertThat(body1.getDescription().getCommands().get(command2).getStatus()).isEqualTo("finished");
        assertThat(body1.getName()).isEqualTo(About.getAppName());
        assertThat(body1.getPath()).isEqualTo("/commanddetached?");
        assertThat(body1.getVersion()).isEqualTo(About.getVersion());
        assertThat(LocalDateTime.parse(body1.getTimestamp(), PATTERN)).isBefore(LocalDateTime.now());
    }

    public Callable<Boolean> isCommandFinished(String command) {
        return () -> {
            ResponseEntity<ApiResponseCommandDescription> responseEntity = getApiResponseCommandDescriptionResponseEntity();
            ApiResponseCommandDescription body = responseEntity.getBody();

            if (body.getDescription().getCommands().get(command) == null)
                return Boolean.FALSE;

            return Boolean.valueOf(body.getDescription().getCommands().get(command).getDuration() > 0F);
        };
    }


    private ResponseEntity<ApiResponseCommandDescription> getApiResponseCommandDescriptionResponseEntity() {
        Map<String, String> headers = new HashMap<>();
        headers.put(CONTENT_TYPE, MediaType.TEXT_PLAIN.toString());
        return this.restTemplate
                .exchange(SERVER_PREFIX + port + "/commanddetached",
                        HttpMethod.GET,
                        httpRequestUtils.getRequestEntityContentTypeAppJson(null, headers),
                        ApiResponseCommandDescription.class);
    }

    private ResponseEntity<ApiResponse> postApiResponseCommandDescriptionResponseEntity(String command, String id) {
        Map<String, String> headers = new HashMap<>();

        return this.restTemplate
                .exchange(SERVER_PREFIX + port + "/commanddetached/" + id,
                        HttpMethod.POST,
                        httpRequestUtils.getRequestEntityContentTypeAppJson(command, headers),
                        ApiResponse.class);
    }
}
