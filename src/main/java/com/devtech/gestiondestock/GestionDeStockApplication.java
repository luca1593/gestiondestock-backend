package com.devtech.gestiondestock;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableSwagger2
@SpringBootApplication
@EnableJpaAuditing
public class GestionDeStockApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(GestionDeStockApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
	}

}
