package com.github.dinuta.estuary.agent.api;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
public class HomeControllerTest {
    private final static String SERVER_PREFIX = "http://localhost:";

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void whenCallingRootUrlThenInformationIsFound() {
        ResponseEntity<String> responseEntity = this.restTemplate
                .getForEntity(SERVER_PREFIX + port + "/",
                        String.class);

        assertThat(responseEntity.getStatusCode().value()).isEqualTo(HttpStatus.FOUND.value());
    }

    @Test
    public void whenCallingSwaggerUiThenInformationIsRetrivedOk() {
        ResponseEntity<String> responseEntity = this.restTemplate
                .getForEntity(SERVER_PREFIX + port + "/swagger-ui.html",
                        String.class);

        String body = responseEntity.getBody();

        assertThat(responseEntity.getStatusCode().value()).isEqualTo(HttpStatus.OK.value());
        assertThat(body).isInstanceOf(String.class);
        assertThat(body).contains("<title>Swagger UI</title>");
    }

    @Test
    public void whenCallingApiDocsThenInformationIsRetrivedOk() {
        ResponseEntity<Map> responseEntity = this.restTemplate
                .getForEntity(SERVER_PREFIX + port + "/apidocs",
                        Map.class);
        Map body = responseEntity.getBody();

        assertThat(responseEntity.getStatusCode().value()).isEqualTo(HttpStatus.OK.value());
        assertThat(body.get("swagger")).isEqualTo("2.0");
        assertThat(((Map) body.get("paths")).size()).isEqualTo(10);
    }

}
