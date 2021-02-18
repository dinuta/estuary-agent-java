package com.github.dinuta.estuary.agent.component;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Authentication {
    @Value("${app.user}")
    @Getter
    private String user;

    @Value("${app.password}")
    @Getter
    private String password;
}
