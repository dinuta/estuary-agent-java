package com.github.dinuta.estuary.agent.configuration;

import com.github.dinuta.estuary.agent.component.About;
import com.github.dinuta.estuary.agent.model.StateHolder;
import com.github.dinuta.estuary.agent.model.api.CommandParallel;
import com.github.dinuta.estuary.agent.service.FluentdService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {
    @Autowired
    private About about;

    @Bean
    public CommandParallel getCommandParallel() {
        return new CommandParallel();
    }

    @Bean
    public StateHolder getStateHolder() {
        return new StateHolder();
    }

    @Bean
    public FluentdService getFluentdService() {
        return new FluentdService(about);
    }
}
