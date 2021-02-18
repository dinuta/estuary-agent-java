package com.github.dinuta.estuary.agent.configuration;

import com.github.dinuta.estuary.agent.component.About;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerDocumentationConfig {

    @Autowired
    private About about;

    ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("estuary-agent")
                .description("Estuary agent will run your shell commands via REST API")
                .license("Apache 2.0")
                .licenseUrl("http://www.apache.org/licenses/LICENSE-2.0.html")
                .termsOfServiceUrl("")
                .version(about.getVersion())
                .contact(new Contact("Catalin Dinuta", "https://dinuta.github.io", "constantin.dinuta@gmail.com"))
                .build();
    }

    @Bean
    public Docket customImplementation() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.github.dinuta.estuary.agent.api"))
                .build()
                .apiInfo(apiInfo());
    }

}
