package com.github.dinuta.estuary.agent.handler;

import com.github.dinuta.estuary.agent.constants.FluentdServiceConstants;
import com.github.dinuta.estuary.agent.service.FluentdService;
import com.github.dinuta.estuary.agent.utils.MessageDumper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.DispatcherType;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class CustomRequestInterceptorAdapter implements HandlerInterceptor {

    @Autowired
    private FluentdService fluentdService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws IOException {

        if (DispatcherType.REQUEST.name().equals(request.getDispatcherType().name())
                && request.getMethod().equals(HttpMethod.GET.name())) {
            fluentdService.emit(FluentdServiceConstants.API, MessageDumper.dumpRequest(request));
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
