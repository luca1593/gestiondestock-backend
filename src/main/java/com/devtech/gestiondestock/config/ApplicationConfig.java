package com.devtech.gestiondestock.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
/*
@EnableWebMvc
@Configuration
@ComponentScan(basePackages="com.devtech.gestiondestock")*/
public class ApplicationConfig implements WebMvcConfigurer {


    public CommonsMultipartResolver getMultipartResolver() {
        return new CommonsMultipartResolver();
    }

}
