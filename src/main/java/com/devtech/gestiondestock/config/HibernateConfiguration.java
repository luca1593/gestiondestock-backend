package com.devtech.gestiondestock.config;

import org.hibernate.Interceptor;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;

public class HibernateConfiguration extends LocalSessionFactoryBean {

    @Override
    public void setEntityInterceptor(Interceptor interceptor) {
        super.setEntityInterceptor((Interceptor) new com.devtech.gestiondestock.interceptor.Interceptor());
    }
}
