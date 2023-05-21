package com.devtech.gestiondestock.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Collections;
import java.util.List;

import static com.devtech.gestiondestock.utils.Constants.APP_ROOT;

@Configuration
@EnableSwagger2
public class SwaggerConfiguration implements WebMvcConfigurer {
    private static final String AUTHORIZATION_HEADER = "Athorization";

    /*
        @Bean
        public Docket api() {
            return new Docket(DocumentationType.SWAGGER_2).select()
                    .apis(RequestHandlerSelectors.basePackage("com.devtech.gestiondestock"))
                    .paths(PathSelectors.any()).build();
        }

    */

    /**
     * Add handlers to serve static resources such as images, js, and, css
     * files from specific locations under web application root, the classpath,
     * and others.
     *
     * @param registry
     * @see ResourceHandlerRegistry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("swagger-ui.html").addResourceLocations("classpath:/ressources/swagger");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/ressources/webjar");
        WebMvcConfigurer.super.addResourceHandlers(registry);
    }

    @Bean
    public Docket api(){
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(
                        new ApiInfoBuilder()
                                .description("Gestion de Stock API V1 documentation")
                                .title("Gestion de Stock REST API")
                                .license("")
                                .licenseUrl("")
                                .termsOfServiceUrl("")
                                .version("1.0.0")
                                .build()
                )
                .groupName("REST API V1")
                .securityContexts(Collections.singletonList(securityContext()))
                .securitySchemes(Collections.singletonList(apiKey()))
                .useDefaultResponseMessages(false)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.devtech.gestiondestock"))
                .paths(PathSelectors.ant(APP_ROOT + "/**"))
                .build();
    }

    private ApiKey apiKey(){
        return new ApiKey("JWT", AUTHORIZATION_HEADER, "header");
    }

    private SecurityContext securityContext(){
        return SecurityContext.builder()
                .securityReferences(defaultAuth())
                .build();
    }

    List<SecurityReference> defaultAuth(){
        AuthorizationScope authorizationScope = new AuthorizationScope(
                "global", "accessEverything"
        );
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        return Collections.singletonList(
                new SecurityReference("JWT", authorizationScopes)
        );
    }
}
