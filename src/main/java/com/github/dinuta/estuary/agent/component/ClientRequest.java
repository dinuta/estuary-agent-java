package com.github.dinuta.estuary.agent.component;

import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
public class ClientRequest {
    private final HttpServletRequest httpServletRequest;

    public ClientRequest(HttpServletRequest httpServletRequest) {
        this.httpServletRequest = httpServletRequest;
    }

    public String getRequestUri() {
        String fullRequestUri = httpServletRequest.getRequestURI() + "?";
        if (httpServletRequest.getQueryString() != null)
            return fullRequestUri + httpServletRequest.getQueryString();

        return fullRequestUri;
    }

    public HttpServletRequest getRequest() {
        return httpServletRequest;
    }
}
