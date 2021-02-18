package com.github.dinuta.estuary.agent.api;

import com.github.dinuta.estuary.agent.api.constants.HeaderConstants;
import com.github.dinuta.estuary.agent.api.utils.HttpRequestUtils;
import com.github.dinuta.estuary.agent.component.About;
import com.github.dinuta.estuary.agent.component.Authentication;
import com.github.dinuta.estuary.agent.constants.ApiResponseCode;
import com.github.dinuta.estuary.agent.constants.ApiResponseMessage;
import com.github.dinuta.estuary.agent.model.api.ApiResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
public class FileApiControllerTest {
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

    @Test
    public void whenCallingGetThenInformationIsRetrivedOk() {
        Map<String, String> headers = new HashMap<>();
        headers.put(HeaderConstants.FILE_PATH, "README.md");

        ResponseEntity<String> responseEntity =
                this.restTemplate.withBasicAuth(auth.getUser(), auth.getPassword())
                        .exchange(SERVER_PREFIX + port + "/file",
                                HttpMethod.GET,
                                httpRequestUtils.getRequestEntityContentTypeAppJson(null, headers),
                                String.class);

        String body = responseEntity.getBody();

        assertThat(responseEntity.getStatusCode().value()).isEqualTo(HttpStatus.OK.value());
        assertThat(body).contains("## Build status");
    }

    @Test
    public void whenFilePathIsMissingThenApiReturnsError() {
        Map<String, String> headers = new HashMap<>();

        ResponseEntity<ApiResponse> responseEntity =
                this.restTemplate.withBasicAuth(auth.getUser(), auth.getPassword())
                        .exchange(SERVER_PREFIX + port + "/file",
                                HttpMethod.GET,
                                httpRequestUtils.getRequestEntityContentTypeAppJson(null, headers),
                                ApiResponse.class);

        ApiResponse body = responseEntity.getBody();

        assertThat(responseEntity.getStatusCode().value()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
        assertThat(body.getCode()).isEqualTo(ApiResponseCode.HTTP_HEADER_NOT_PROVIDED.getCode());
        assertThat(body.getMessage()).isEqualTo(
                String.format(ApiResponseMessage.getMessage(ApiResponseCode.HTTP_HEADER_NOT_PROVIDED.getCode()), HeaderConstants.FILE_PATH));
        assertThat(body.getDescription().toString()).contains(String.format(ApiResponseMessage.getMessage(ApiResponseCode.HTTP_HEADER_NOT_PROVIDED.getCode()), HeaderConstants.FILE_PATH));
        assertThat(body.getName()).isEqualTo(about.getAppName());
        assertThat(body.getVersion()).isEqualTo(about.getVersion());
        assertThat(LocalDateTime.parse(body.getTimestamp(), PATTERN)).isBefore(LocalDateTime.now());

    }

    @Test
    public void whenFilePathIsWrongThenApiReturnsError() {
        Map<String, String> headers = new HashMap<>();
        headers.put(HeaderConstants.FILE_PATH, "whateverinvalid");

        ResponseEntity<ApiResponse> responseEntity =
                this.restTemplate.withBasicAuth(auth.getUser(), auth.getPassword())
                        .exchange(SERVER_PREFIX + port + "/file",
                                HttpMethod.GET,
                                httpRequestUtils.getRequestEntityContentTypeAppJson(null, headers),
                                ApiResponse.class);

        ApiResponse body = responseEntity.getBody();

        assertThat(responseEntity.getStatusCode().value()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
        assertThat(body.getCode()).isEqualTo(ApiResponseCode.GET_FILE_FAILURE.getCode());
        assertThat(body.getMessage()).isEqualTo(
                String.format(ApiResponseMessage.getMessage(ApiResponseCode.GET_FILE_FAILURE.getCode())));
        assertThat(body.getDescription().toString()).contains("Exception");
        assertThat(body.getName()).isEqualTo(about.getAppName());
        assertThat(body.getVersion()).isEqualTo(about.getVersion());
        assertThat(LocalDateTime.parse(body.getTimestamp(), PATTERN)).isBefore(LocalDateTime.now());
    }

    @Test
    public void whenUploadingFileAndFilePathIsWrongThenApiReturnsError() {
        Map<String, String> headers = new HashMap<>();
        headers.put(HeaderConstants.FILE_PATH, "whateverinvalid/a/imlazytoday/lazy.txt");

        ResponseEntity<ApiResponse> responseEntity =
                this.restTemplate.withBasicAuth(auth.getUser(), auth.getPassword())
                        .exchange(SERVER_PREFIX + port + "/file",
                                HttpMethod.PUT,
                                httpRequestUtils.getRequestEntityContentTypeAppJson("doesnotmatter", headers),
                                ApiResponse.class);

        ApiResponse body = responseEntity.getBody();

        assertThat(responseEntity.getStatusCode().value()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
        assertThat(body.getCode()).isEqualTo(ApiResponseCode.UPLOAD_FILE_FAILURE.getCode());
        assertThat(body.getMessage()).isEqualTo(
                String.format(ApiResponseMessage.getMessage(ApiResponseCode.UPLOAD_FILE_FAILURE.getCode())));
        assertThat(body.getDescription().toString()).contains("Exception");
        assertThat(body.getName()).isEqualTo(about.getAppName());
        assertThat(body.getVersion()).isEqualTo(about.getVersion());
        assertThat(LocalDateTime.parse(body.getTimestamp(), PATTERN)).isBefore(LocalDateTime.now());
    }

    @Test
    public void whenUploadingFileAndFilePathIsCorrectThenApiReturnsSuccess() {
        Map<String, String> headers = new HashMap<>();
        headers.put(HeaderConstants.FILE_PATH, "config.properties");

        ResponseEntity<ApiResponse> responseEntity =
                this.restTemplate.withBasicAuth(auth.getUser(), auth.getPassword())
                        .exchange(SERVER_PREFIX + port + "/file",
                                HttpMethod.PUT,
                                httpRequestUtils.getRequestEntityContentTypeAppJson("{\"ip\": \"localhost\"}", headers),
                                ApiResponse.class);

        ApiResponse body = responseEntity.getBody();

        assertThat(responseEntity.getStatusCode().value()).isEqualTo(HttpStatus.OK.value());
        assertThat(body.getCode()).isEqualTo(ApiResponseCode.SUCCESS.getCode());
        assertThat(body.getMessage()).isEqualTo(
                String.format(ApiResponseMessage.getMessage(ApiResponseCode.SUCCESS.getCode())));
        assertThat(body.getDescription()).isEqualTo(
                String.format(ApiResponseMessage.getMessage(ApiResponseCode.SUCCESS.getCode())));
        assertThat(body.getName()).isEqualTo(about.getAppName());
        assertThat(body.getPath()).isEqualTo("/file?");
        assertThat(body.getVersion()).isEqualTo(about.getVersion());
        assertThat(LocalDateTime.parse(body.getTimestamp(), PATTERN)).isBefore(LocalDateTime.now());
    }

    @Test
    public void whenUploadingFileAndBodyIsEmptyThenApiReturnsSuccess() {
        Map<String, String> headers = new HashMap<>();
        headers.put(HeaderConstants.FILE_PATH, "myEmptyFile.txt");

        ResponseEntity<ApiResponse> responseEntity =
                this.restTemplate.withBasicAuth(auth.getUser(), auth.getPassword())
                        .exchange(SERVER_PREFIX + port + "/file",
                                HttpMethod.POST,
                                httpRequestUtils.getRequestEntityContentTypeAppJson(null, headers),
                                ApiResponse.class);

        ApiResponse body = responseEntity.getBody();

        assertThat(responseEntity.getStatusCode().value()).isEqualTo(HttpStatus.OK.value());
        assertThat(body.getCode()).isEqualTo(ApiResponseCode.SUCCESS.getCode());
        assertThat(body.getMessage()).isEqualTo(
                String.format(ApiResponseMessage.getMessage(ApiResponseCode.SUCCESS.getCode())));
        assertThat(body.getDescription()).isEqualTo(
                String.format(ApiResponseMessage.getMessage(ApiResponseCode.SUCCESS.getCode())));
        assertThat(body.getName()).isEqualTo(about.getAppName());
        assertThat(body.getPath()).isEqualTo("/file?");
        assertThat(body.getVersion()).isEqualTo(about.getVersion());
        assertThat(LocalDateTime.parse(body.getTimestamp(), PATTERN)).isBefore(LocalDateTime.now());
    }
}
