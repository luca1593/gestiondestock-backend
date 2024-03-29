package com.devtech.gestiondestock.config;

import com.devtech.gestiondestock.services.auth.ApplicationUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * @author luca
 */
@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Autowired
    private ApplicationUserDetailsService applicationUserDetailsService;

    @Autowired
    private ApplicationRequestFilter applicationRequestFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(this.applicationUserDetailsService)
                .passwordEncoder(passwordEncoder());
        http.cors(AbstractHttpConfigurer::disable);
        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers(
                                "/**/authenticate", "/**/entreprise/create", "/v2/api-docs",
                                "/swagger-resources", "/swagger-resources/**", "/swagger-ui",
                                "/configuration/security", "/configuration/ui", "/**/swagger-ui.html",
                                "/webjars/**", "/**/springfox-swagger-ui.html", "/**/springfox-swagger-ui/**",
                                "/v3/api-docs/**", "/swagger-ui/**"
                        ).permitAll()
                        .anyRequest().authenticated())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.addFilterBefore(this.applicationRequestFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            final AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

}

