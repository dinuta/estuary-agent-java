package com.github.dinuta.estuary.agent.config;

import com.github.dinuta.estuary.agent.model.api.CommandParallel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {

    @Bean
    public CommandParallel getCommandParallel() {
        return new CommandParallel();
    }
}
