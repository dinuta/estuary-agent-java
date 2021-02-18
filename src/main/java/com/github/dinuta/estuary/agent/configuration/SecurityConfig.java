package com.github.dinuta.estuary.agent.configuration;

import com.github.dinuta.estuary.agent.component.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import static com.github.dinuta.estuary.agent.constants.EnvConstants.HTTP_AUTH_PASSWORD;
import static com.github.dinuta.estuary.agent.constants.EnvConstants.HTTP_AUTH_USER;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Profile("!test")
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    Authentication auth;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .cors()
                .and()
                .csrf().disable()
                .authorizeRequests()
                .anyRequest()
                .authenticated()
                .and()
                .httpBasic()
                .and()
                .formLogin()
                .permitAll();
    }

    @Bean
    @Override
    public UserDetailsService userDetailsService() {
        String username = System.getenv(HTTP_AUTH_USER) != null ? System.getenv(HTTP_AUTH_USER) : auth.getUser();
        String password = System.getenv(HTTP_AUTH_PASSWORD) != null ? System.getenv(HTTP_AUTH_PASSWORD) : auth.getPassword();

        UserDetails userDetails =
                User.withDefaultPasswordEncoder()
                        .username(username)
                        .password(password)
                        .roles("USER")
                        .build();

        return new InMemoryUserDetailsManager(userDetails);
    }
}
