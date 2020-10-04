package com.github.dinuta.estuary.agent.model.logging;

import java.util.LinkedHashMap;

public class ParentMessage {

    public LinkedHashMap<String, String> headers;
    public Object body;

    public LinkedHashMap getHeaders() {
        return headers;
    }

    public void setHeaders(LinkedHashMap headers) {
        this.headers = headers;
    }

    public Object getBody() {
        return body;
    }

    public void setBody(Object body) {
        this.body = body;
    }
}
