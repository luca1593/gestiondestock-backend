package com.devtech.gestiondestock;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * @author luca
 */
@ComponentScan
@SpringBootApplication
@EnableJpaAuditing
public class GestionDeStockApplication {

	public static void main(String[] args) {
		SpringApplication.run(GestionDeStockApplication.class, args);
	}

}
