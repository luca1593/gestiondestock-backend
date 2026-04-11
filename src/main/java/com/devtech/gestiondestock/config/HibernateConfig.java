package com.devtech.gestiondestock.config;

import com.devtech.gestiondestock.interceptor.Interceptor;
import org.hibernate.cfg.AvailableSettings;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HibernateConfig {

    @Bean
    public HibernatePropertiesCustomizer hibernatePropertiesCustomizer(Interceptor interceptor) {
        return hibernateProperties -> hibernateProperties.put(
            AvailableSettings.STATEMENT_INSPECTOR,
            interceptor
        );
    }
}
