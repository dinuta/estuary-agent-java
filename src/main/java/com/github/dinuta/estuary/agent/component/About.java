package com.github.dinuta.estuary.agent.component;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
public class About {
    @Getter
    private final String appName = "estuary-agent";
    @Value("${app.version}")
    @Getter
    private String version;
}
