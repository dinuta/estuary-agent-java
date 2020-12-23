package com.github.dinuta.estuary.agent.api;

import com.github.dinuta.estuary.agent.api.utils.HttpRequestUtils;
import com.github.dinuta.estuary.agent.component.VirtualEnvironment;
import com.github.dinuta.estuary.agent.constants.About;
import com.github.dinuta.estuary.agent.constants.ApiResponseCode;
import com.github.dinuta.estuary.agent.constants.ApiResponseMessage;
import com.github.dinuta.estuary.agent.model.api.ApiResponse;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static com.github.dinuta.estuary.agent.constants.DateTimeConstants.PATTERN;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class EnvApiControllerTest {
    private final static String SERVER_PREFIX = "http://localhost:";

    @LocalServerPort
    private int port;

    @Autowired
    private HttpRequestUtils httpRequestUtils;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void whenCallingGetThenInformationIsRetrivedOk() {
        ResponseEntity<ApiResponse> responseEntity =
                this.restTemplate.getForEntity(SERVER_PREFIX + port + "/env", ApiResponse.class);

        ApiResponse body = responseEntity.getBody();

        assertThat(responseEntity.getStatusCode().value()).isEqualTo(HttpStatus.OK.value());
        assertThat(body.getCode()).isEqualTo(ApiResponseCode.SUCCESS.getCode());
        assertThat(body.getMessage()).isEqualTo(ApiResponseMessage.getMessage(ApiResponseCode.SUCCESS.getCode()));
        assertThat(body.getDescription()).isInstanceOf(Map.class);
        assertThat(((Map) body.getDescription()).get("PATH")).isNotEqualTo("");
        assertThat(body.getName()).isEqualTo(About.getAppName());
        assertThat(body.getPath()).isEqualTo("/env?");
        assertThat(body.getVersion()).isEqualTo(About.getVersion());
        assertThat(LocalDateTime.parse(body.getTimestamp(), PATTERN)).isBefore(LocalDateTime.now());
    }

    @Test
    public void whenGettingExistentEnvVarThenInformationIsRetrivedOk() {
        String envVar = "PATH";
        ResponseEntity<ApiResponse> responseEntity =
                this.restTemplate.getForEntity(SERVER_PREFIX + port + "/env/" + envVar, ApiResponse.class);

        ApiResponse body = responseEntity.getBody();

        assertThat(responseEntity.getStatusCode().value()).isEqualTo(HttpStatus.OK.value());
        assertThat(body.getCode()).isEqualTo(ApiResponseCode.SUCCESS.getCode());
        assertThat(body.getMessage()).isEqualTo(ApiResponseMessage.getMessage(ApiResponseCode.SUCCESS.getCode()));
        assertThat(body.getDescription()).isInstanceOf(String.class);
        assertThat(body.getDescription()).isNotEqualTo("");
        assertThat(body.getName()).isEqualTo(About.getAppName());
        assertThat(body.getVersion()).isEqualTo(About.getVersion());
        assertThat(LocalDateTime.parse(body.getTimestamp(), PATTERN)).isBefore(LocalDateTime.now());
    }

    @Test
    public void whenSettingExternalEnvVarsWithInvalidBodyWithRestAPIThenError() {
        String envVars = "{whatever_invalid_json}";
        ResponseEntity<ApiResponse> responseEntity = this.restTemplate
                .exchange(SERVER_PREFIX + port + "/env",
                        HttpMethod.POST,
                        httpRequestUtils.getRequestEntityContentTypeAppJson(envVars, new HashMap<>()),
                        ApiResponse.class);

        ApiResponse body = responseEntity.getBody();

        assertThat(responseEntity.getStatusCode().value()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
        assertThat(body.getCode()).isEqualTo(ApiResponseCode.SET_ENV_VAR_FAILURE.getCode());
        assertThat(body.getMessage()).isEqualTo(
                String.format(ApiResponseMessage.getMessage(ApiResponseCode.SET_ENV_VAR_FAILURE.getCode()), envVars), envVars);
        assertThat(body.getDescription()).isInstanceOf(String.class);
        assertThat(body.getDescription().toString()).contains("Exception");
        assertThat(body.getName()).isEqualTo(About.getAppName());
        assertThat(body.getVersion()).isEqualTo(About.getVersion());
        assertThat(LocalDateTime.parse(body.getTimestamp(), PATTERN)).isBefore(LocalDateTime.now());
    }

    @Test
    public void whenGettingNotExistentEnvVarThenValueIsNull() {
        String envVar = "whatever";
        ResponseEntity<ApiResponse> responseEntity =
                this.restTemplate.getForEntity(SERVER_PREFIX + port + "/env/" + envVar, ApiResponse.class);

        ApiResponse body = responseEntity.getBody();

        assertThat(responseEntity.getStatusCode().value()).isEqualTo(HttpStatus.OK.value());
        assertThat(body.getCode()).isEqualTo(ApiResponseCode.SUCCESS.getCode());
        assertThat(body.getMessage()).isEqualTo(ApiResponseMessage.getMessage(ApiResponseCode.SUCCESS.getCode()));
        assertThat(body.getDescription()).isEqualTo(null);
        assertThat(body.getName()).isEqualTo(About.getAppName());
        assertThat(body.getVersion()).isEqualTo(About.getVersion());
        assertThat(LocalDateTime.parse(body.getTimestamp(), PATTERN)).isBefore(LocalDateTime.now());
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                    "FOO1;BAR1",
                    "FOO2;BAR2"
            }
    )
    @Order(1)
    public void whenSettingExternalEnvVarsFromFileThenInformationIsRetrivedOk(String envInfo) {
        String envVar = envInfo.split(";")[0];
        String expectedValue = envInfo.split(";")[1];
        ResponseEntity<ApiResponse> responseEntity =
                this.restTemplate.getForEntity(SERVER_PREFIX + port + "/env/" + envVar, ApiResponse.class);

        ApiResponse body = responseEntity.getBody();

        assertThat(responseEntity.getStatusCode().value()).isEqualTo(HttpStatus.OK.value());
        assertThat(body.getCode()).isEqualTo(ApiResponseCode.SUCCESS.getCode());
        assertThat(body.getMessage()).isEqualTo(ApiResponseMessage.getMessage(ApiResponseCode.SUCCESS.getCode()));
        assertThat(body.getDescription()).isInstanceOf(String.class);
        assertThat(body.getDescription()).isEqualTo(expectedValue);
        assertThat(body.getName()).isEqualTo(About.getAppName());
        assertThat(body.getVersion()).isEqualTo(About.getVersion());
        assertThat(LocalDateTime.parse(body.getTimestamp(), PATTERN)).isBefore(LocalDateTime.now());
    }

    @Test
    @Order(2)
    public void whenSettingExternalEnvVarsFromFileAndItsASystemOneThenItDoesntGetOverwritten() {
        String envVar = "JAVA_HOME";
        String notExpectedValue = "this_value_wont_be_injected_because_its_an_existing_system_env_var";
        ResponseEntity<ApiResponse> responseEntity =
                this.restTemplate.getForEntity(SERVER_PREFIX + port + "/env/" + envVar, ApiResponse.class);

        ApiResponse body = responseEntity.getBody();

        assertThat(responseEntity.getStatusCode().value()).isEqualTo(HttpStatus.OK.value());
        assertThat(body.getCode()).isEqualTo(ApiResponseCode.SUCCESS.getCode());
        assertThat(body.getMessage()).isEqualTo(ApiResponseMessage.getMessage(ApiResponseCode.SUCCESS.getCode()));
        assertThat(body.getDescription()).isInstanceOf(String.class);
        assertThat(body.getDescription()).isNotEqualTo(notExpectedValue);
        assertThat(body.getName()).isEqualTo(About.getAppName());
        assertThat(body.getVersion()).isEqualTo(About.getVersion());
        assertThat(LocalDateTime.parse(body.getTimestamp(), PATTERN)).isBefore(LocalDateTime.now());
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                    "FOO1;BARx",
                    "FOO3;BAR3"
            }
    )
    @Order(3)
    public void whenSettingExternalEnvVarsWithRestAPIThenInformationIsRetrivedOk(String envVars) {
        String envVarName = envVars.split(";")[0];
        String expectedEnvVarValue = envVars.split(";")[1];
        String envVarsJson = String.format("{\"%s\":\"%s\"}", envVarName, expectedEnvVarValue);
        ResponseEntity<ApiResponse> responseEntity = this.restTemplate
                .exchange(SERVER_PREFIX + port + "/env",
                        HttpMethod.POST,
                        httpRequestUtils.getRequestEntityContentTypeAppJson(envVarsJson, new HashMap<>()),
                        ApiResponse.class);

        ApiResponse body = responseEntity.getBody();

        assertThat(responseEntity.getStatusCode().value()).isEqualTo(HttpStatus.OK.value());
        assertThat(body.getCode()).isEqualTo(ApiResponseCode.SUCCESS.getCode());
        assertThat(body.getMessage()).isEqualTo(ApiResponseMessage.getMessage(ApiResponseCode.SUCCESS.getCode()));
        assertThat(body.getDescription()).isInstanceOf(Map.class);
        assertThat(body.getName()).isEqualTo(About.getAppName());
        assertThat(body.getVersion()).isEqualTo(About.getVersion());
        assertThat(LocalDateTime.parse(body.getTimestamp(), PATTERN)).isBefore(LocalDateTime.now());

        responseEntity =
                this.restTemplate.getForEntity(SERVER_PREFIX + port + "/env", ApiResponse.class);
        body = responseEntity.getBody();

        assertThat(body.getDescription()).isInstanceOf(Map.class);
        assertThat(((Map) body.getDescription()).get(envVarName)).isEqualTo(expectedEnvVarValue);
    }



    @ParameterizedTest
    @ValueSource(
            strings = {
                    "FOO1;BARx",
                    "FOO3;BAR3"
            }
    )
    @Order(4)
    public void whenSettingExternalEnvVarsWithRestAPIThenSystemEnvVarsAreNotOverwritten(String envVars) {
        String envVarName = envVars.split(";")[0];
        String expectedEnvVarValue = envVars.split(";")[1];
        String attemptedShellEnvVarValue = "must_be_immutable";

        String envVarsJson = String.format("{\"%s\":\"%s\", \"PATH\": \"%s\"}",
                envVarName, expectedEnvVarValue, attemptedShellEnvVarValue);
        ResponseEntity<ApiResponse> responseEntity = this.restTemplate
                .exchange(SERVER_PREFIX + port + "/env",
                        HttpMethod.POST,
                        httpRequestUtils.getRequestEntityContentTypeAppJson(envVarsJson, new HashMap<>()),
                        ApiResponse.class);

        ApiResponse body = responseEntity.getBody();

        assertThat(responseEntity.getStatusCode().value()).isEqualTo(HttpStatus.OK.value());
        assertThat(body.getCode()).isEqualTo(ApiResponseCode.SUCCESS.getCode());
        assertThat(body.getMessage()).isEqualTo(ApiResponseMessage.getMessage(ApiResponseCode.SUCCESS.getCode()));
        assertThat(body.getDescription()).isInstanceOf(Map.class);
        assertThat(((Map) body.getDescription()).size()).isEqualTo(1);
        assertThat(body.getName()).isEqualTo(About.getAppName());
        assertThat(body.getVersion()).isEqualTo(About.getVersion());
        assertThat(LocalDateTime.parse(body.getTimestamp(), PATTERN)).isBefore(LocalDateTime.now());

        responseEntity =
                this.restTemplate.getForEntity(SERVER_PREFIX + port + "/env", ApiResponse.class);
        body = responseEntity.getBody();

        assertThat(body.getDescription()).isInstanceOf(Map.class);
        assertThat(((Map) body.getDescription()).get(envVarName)).isEqualTo(expectedEnvVarValue);
        assertThat(((Map) body.getDescription()).get("SHELL")).isNotEqualTo(attemptedShellEnvVarValue);
    }

    @Test
    @Order(9)
    public void whenSettingVirtualEnvVarsThenAHardLimitIsReached() {
        final int VIRTUAL_ENV_VARS_LIMIT_SIZE = VirtualEnvironment.VIRTUAL_ENVIRONMENT_MAX_SIZE;

        for (int i = 0; i < 2 * VIRTUAL_ENV_VARS_LIMIT_SIZE; i++) {
            String envVarsJson = String.format("{\"%s\":\"%s\"}", i, i);
            this.restTemplate
                    .exchange(SERVER_PREFIX + port + "/env",
                            HttpMethod.POST,
                            httpRequestUtils.getRequestEntityContentTypeAppJson(envVarsJson, new HashMap<>()),
                            ApiResponse.class);
        }

        ResponseEntity responseEntity =
                this.restTemplate.getForEntity(SERVER_PREFIX + port + "/env", ApiResponse.class);
        ApiResponse body = (ApiResponse) responseEntity.getBody();

        assertThat(body.getDescription()).isInstanceOf(Map.class);
        assertThat(((Map<String, String>) body.getDescription()).get(String.valueOf(VIRTUAL_ENV_VARS_LIMIT_SIZE))).isEqualTo(null);
    }
}
