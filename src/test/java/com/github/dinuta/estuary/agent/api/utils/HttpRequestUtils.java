package com.github.dinuta.estuary.agent.api.utils;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.Arrays;
import java.util.Map;

public class HttpRequestUtils {

    public HttpEntity<String> getRequestEntityContentTypeAppJson(String body, Map<String, String> headersMap) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        headersMap.forEach((k, v) -> {
            headers.add(k, v);
        });

        return new HttpEntity<>(body, headers);
    }

    public HttpEntity<String> getRequestEntityJsonContentTypeAppText(String body, Map<String, String> headersMap) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.TEXT_PLAIN);
        headersMap.forEach((k, v) -> {
            headers.add(k, v);
        });

        return new HttpEntity<>(body, headers);
    }
}
