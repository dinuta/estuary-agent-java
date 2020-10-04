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
public class XRequestIdTest {
    private final static String SERVER_PREFIX = "http://localhost:";

    @LocalServerPort
    private int port;

    @Autowired
    private HttpRequestUtils httpRequestUtils;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void whenCallingWithXIdSetFromClientThenTheServersReturnsItBack() {
        String xReqId = "my-provided-value";
        Map<String, String> headers = new HashMap<>();
        headers.put(HeaderConstants.X_REQUEST_ID, xReqId);

        ResponseEntity<ApiResponse> responseEntity =
                this.restTemplate
                        .exchange(SERVER_PREFIX + port + "/about",
                                HttpMethod.GET,
                                httpRequestUtils.getRequestEntityContentTypeAppJson(null, headers),
                                ApiResponse.class);

        ApiResponse body = responseEntity.getBody();

        assertThat(responseEntity.getStatusCode().value()).isEqualTo(HttpStatus.OK.value());
        assertThat(responseEntity.getHeaders().get(HeaderConstants.X_REQUEST_ID).get(0)).isEqualTo(xReqId); // <--
        assertThat(body.getCode()).isEqualTo(ApiResponseConstants.SUCCESS);
        assertThat(body.getMessage()).isEqualTo(ApiResponseMessage.getMessage(ApiResponseConstants.SUCCESS));
        assertThat(body.getDescription()).isEqualTo(About.getAppName());
        assertThat(body.getName()).isEqualTo(About.getAppName());
        assertThat(body.getVersion()).isEqualTo(About.getVersion());
        assertThat(LocalDateTime.parse(body.getTimestamp(), PATTERN)).isBefore(LocalDateTime.now());
    }

    @Test
    public void whenCallingWithXIdNotSetFromClientThenTheServersReturnsANewUUIDOne() {
        Map<String, String> headers = new HashMap<>();

        ResponseEntity<ApiResponse> responseEntity =
                this.restTemplate
                        .exchange(SERVER_PREFIX + port + "/about",
                                HttpMethod.GET,
                                httpRequestUtils.getRequestEntityContentTypeAppJson(null, headers),
                                ApiResponse.class);

        ApiResponse body = responseEntity.getBody();

        assertThat(responseEntity.getStatusCode().value()).isEqualTo(HttpStatus.OK.value());
        assertThat(responseEntity.getHeaders().get(HeaderConstants.X_REQUEST_ID).get(0)).isInstanceOf(String.class); // <--
        assertThat(responseEntity.getHeaders().get(HeaderConstants.X_REQUEST_ID).get(0).length()).isEqualTo(36); // <--
        assertThat(body.getCode()).isEqualTo(ApiResponseConstants.SUCCESS);
        assertThat(body.getMessage()).isEqualTo(ApiResponseMessage.getMessage(ApiResponseConstants.SUCCESS));
        assertThat(body.getDescription()).isEqualTo(About.getAppName());
        assertThat(body.getName()).isEqualTo(About.getAppName());
        assertThat(body.getVersion()).isEqualTo(About.getVersion());
        assertThat(LocalDateTime.parse(body.getTimestamp(), PATTERN)).isBefore(LocalDateTime.now());
    }

}
