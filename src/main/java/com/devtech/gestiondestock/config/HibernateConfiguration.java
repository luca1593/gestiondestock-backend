package com.devtech.gestiondestock.config;

import com.devtech.gestiondestock.interceptor.Interceptor;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;

import java.util.Map;

/**
 * @author luca
 */
public class HibernateConfiguration implements HibernatePropertiesCustomizer {

    private final Interceptor interceptor;

    public HibernateConfiguration() {
        this.interceptor = new Interceptor();
    }

    @Override
    public void customize(Map<String, Object> hibernateProperties) {
        hibernateProperties.put("hibernate.session_factory.interceptor", this.interceptor);
    }
}
