package com.devtech.gestiondestock.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/*@Configuration
@EnableJpaRepositories(basePackages = "com.devtech.gestiondestock.repository")
@PropertySource("persistence-generic-entity.yml")
@EnableTransactionManagement*/
public class H2JpaConfig {
}
