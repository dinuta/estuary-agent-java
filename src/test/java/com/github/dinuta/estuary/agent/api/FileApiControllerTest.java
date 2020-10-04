package com.github.dinuta.estuary.agent.api;

import com.github.dinuta.estuary.agent.api.constants.HeaderConstants;
import com.github.dinuta.estuary.agent.api.utils.HttpRequestUtils;
import com.github.dinuta.estuary.agent.constants.About;
import com.github.dinuta.estuary.agent.constants.ApiResponseConstants;
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

    @Test
    public void whenCallingGetThenInformationIsRetrivedOk() {
        Map<String, String> headers = new HashMap<>();
        headers.put(HeaderConstants.FILE_PATH, "README.md");

        ResponseEntity<String> responseEntity =
                this.restTemplate
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
                this.restTemplate
                        .exchange(SERVER_PREFIX + port + "/file",
                                HttpMethod.GET,
                                httpRequestUtils.getRequestEntityContentTypeAppJson(null, headers),
                                ApiResponse.class);

        ApiResponse body = responseEntity.getBody();

        assertThat(responseEntity.getStatusCode().value()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(body.getCode()).isEqualTo(ApiResponseConstants.HTTP_HEADER_NOT_PROVIDED);
        assertThat(body.getMessage()).isEqualTo(
                String.format(ApiResponseMessage.getMessage(ApiResponseConstants.HTTP_HEADER_NOT_PROVIDED), HeaderConstants.FILE_PATH));
        assertThat(body.getDescription()).isEqualTo(
                String.format(ApiResponseMessage.getMessage(ApiResponseConstants.HTTP_HEADER_NOT_PROVIDED), HeaderConstants.FILE_PATH));
        assertThat(body.getName()).isEqualTo(About.getAppName());
        assertThat(body.getVersion()).isEqualTo(About.getVersion());
        assertThat(LocalDateTime.parse(body.getTimestamp(), PATTERN)).isBefore(LocalDateTime.now());

    }

    @Test
    public void whenFilePathIsWrongThenApiReturnsError() {
        Map<String, String> headers = new HashMap<>();
        headers.put(HeaderConstants.FILE_PATH, "whateverinvalid");

        ResponseEntity<ApiResponse> responseEntity =
                this.restTemplate
                        .exchange(SERVER_PREFIX + port + "/file",
                                HttpMethod.GET,
                                httpRequestUtils.getRequestEntityContentTypeAppJson(null, headers),
                                ApiResponse.class);

        ApiResponse body = responseEntity.getBody();

        assertThat(responseEntity.getStatusCode().value()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(body.getCode()).isEqualTo(ApiResponseConstants.GET_FILE_FAILURE);
        assertThat(body.getMessage()).isEqualTo(
                String.format(ApiResponseMessage.getMessage(ApiResponseConstants.GET_FILE_FAILURE)));
        assertThat(body.getDescription().toString()).contains("Exception");
        assertThat(body.getName()).isEqualTo(About.getAppName());
        assertThat(body.getVersion()).isEqualTo(About.getVersion());
        assertThat(LocalDateTime.parse(body.getTimestamp(), PATTERN)).isBefore(LocalDateTime.now());
    }

    @Test
    public void whenUploadingFileAndFilePathIsWrongThenApiReturnsError() {
        Map<String, String> headers = new HashMap<>();
        headers.put(HeaderConstants.FILE_PATH, "whateverinvalid/a/imlazytoday/lazy.txt");

        ResponseEntity<ApiResponse> responseEntity =
                this.restTemplate
                        .exchange(SERVER_PREFIX + port + "/file",
                                HttpMethod.PUT,
                                httpRequestUtils.getRequestEntityContentTypeAppJson("doesnotmatter", headers),
                                ApiResponse.class);

        ApiResponse body = responseEntity.getBody();

        assertThat(responseEntity.getStatusCode().value()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(body.getCode()).isEqualTo(ApiResponseConstants.UPLOAD_FILE_FAILURE);
        assertThat(body.getMessage()).isEqualTo(
                String.format(ApiResponseMessage.getMessage(ApiResponseConstants.UPLOAD_FILE_FAILURE)));
        assertThat(body.getDescription().toString()).contains("Exception");
        assertThat(body.getName()).isEqualTo(About.getAppName());
        assertThat(body.getVersion()).isEqualTo(About.getVersion());
        assertThat(LocalDateTime.parse(body.getTimestamp(), PATTERN)).isBefore(LocalDateTime.now());
    }

    @Test
    public void whenUploadingFileAndFilePathIsCorrectThenApiReturnsSuccess() {
        Map<String, String> headers = new HashMap<>();
        headers.put(HeaderConstants.FILE_PATH, "config.properties");

        ResponseEntity<ApiResponse> responseEntity =
                this.restTemplate
                        .exchange(SERVER_PREFIX + port + "/file",
                                HttpMethod.PUT,
                                httpRequestUtils.getRequestEntityContentTypeAppJson("{\"ip\": \"localhost\"}", headers),
                                ApiResponse.class);

        ApiResponse body = responseEntity.getBody();

        assertThat(responseEntity.getStatusCode().value()).isEqualTo(HttpStatus.OK.value());
        assertThat(body.getCode()).isEqualTo(ApiResponseConstants.SUCCESS);
        assertThat(body.getMessage()).isEqualTo(
                String.format(ApiResponseMessage.getMessage(ApiResponseConstants.SUCCESS)));
        assertThat(body.getDescription()).isEqualTo(
                String.format(ApiResponseMessage.getMessage(ApiResponseConstants.SUCCESS)));
        assertThat(body.getName()).isEqualTo(About.getAppName());
        assertThat(body.getPath()).isEqualTo("/file?");
        assertThat(body.getVersion()).isEqualTo(About.getVersion());
        assertThat(LocalDateTime.parse(body.getTimestamp(), PATTERN)).isBefore(LocalDateTime.now());
    }
}
