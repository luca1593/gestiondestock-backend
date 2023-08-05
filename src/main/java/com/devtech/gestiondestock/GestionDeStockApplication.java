package com.devtech.gestiondestock;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author luca
 */
@Configuration
@ComponentScan
@SpringBootApplication
@EnableJpaAuditing
public class GestionDeStockApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(GestionDeStockApplication.class, args);
	}

	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**").allowedOrigins("http://localhost:4200");
				registry.addMapping("/**").allowedOrigins("http://localhost:4200/**");
				registry.addMapping("/**").allowedOrigins("http://www.gestion-stock.mg");
			}
		};
	}
}
