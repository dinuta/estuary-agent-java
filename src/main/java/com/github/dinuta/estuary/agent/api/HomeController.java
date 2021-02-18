package com.github.dinuta.estuary.agent.api;

import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Home redirection to swagger api documentation
 */
@Controller
@Api(tags = {"estuary-agent"}, description = "index page")
public class HomeController {
    private static final Logger log = LoggerFactory.getLogger(HomeController.class);

    @GetMapping(value = "/")
    public String index() {
        log.info("redirect:swagger-ui/");
        return "redirect:swagger-ui/";
    }
}
