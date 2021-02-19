package com.github.dinuta.estuary.agent.api;

import com.github.dinuta.estuary.agent.component.VirtualEnvironment;
import com.github.dinuta.estuary.agent.constants.HeaderConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

import static com.github.dinuta.estuary.agent.constants.EnvConstants.HTTP_AUTH_TOKEN;

@Component
public class ApiOriginFilter extends GenericFilterBean {
    private static final Logger log = LoggerFactory.getLogger(ApiOriginFilter.class);

    @Autowired
    private VirtualEnvironment environment;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        httpResponse.addHeader("Access-Control-Allow-Origin", "*");
        httpResponse.addHeader("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT");
        httpResponse.addHeader("Access-Control-Allow-Headers", "Content-Type");

        String xRequestId = ((HttpServletRequest) request).getHeader(HeaderConstants.X_REQUEST_ID);
        String tokenHeader = ((HttpServletRequest) request).getHeader(HeaderConstants.TOKEN);

        if (xRequestId == null) {
            xRequestId = UUID.randomUUID().toString();
        }
        httpResponse.addHeader(HeaderConstants.X_REQUEST_ID, xRequestId);
        log.debug(HeaderConstants.TOKEN + " Header: " + tokenHeader);
        log.debug(HeaderConstants.X_REQUEST_ID + " : " + xRequestId);

        if (!(String.valueOf(tokenHeader)
                .equals(String.valueOf(environment.getEnvAndVirtualEnv().get(HTTP_AUTH_TOKEN))))) {
            httpResponse.sendError(HttpStatus.UNAUTHORIZED.value());
            return;
        }

        chain.doFilter(httpRequest, httpResponse);
    }

    @Override
    public void destroy() {
    }
}
