package com.devtech.gestiondestock.config;

import com.devtech.gestiondestock.exception.ErrorsCode;
import com.devtech.gestiondestock.handlers.ErrorsDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

/**
 * @author luca
 * Répond en JSON (401) lorsqu'une requête non authentifiée atteint un endpoint protégé,
 * au lieu du 403 par défaut de Spring Security. Corps identique à celui produit par
 * {@code RestExceptionHandler}.
 */
@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    public RestAuthenticationEntryPoint(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        ErrorsDto body = ErrorsDto.builder()
                .httpCode(HttpStatus.UNAUTHORIZED.value())
                .errorsCode(ErrorsCode.BAD_CREDENTIALS)
                .message("Authentification requise")
                .errors(List.of("Jeton d'authentification absent, invalide ou expiré"))
                .build();

        objectMapper.writeValue(response.getWriter(), body);
    }
}
