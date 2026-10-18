package com.devtech.gestiondestock.config;

import com.devtech.gestiondestock.exception.ErrorsCode;
import com.devtech.gestiondestock.handlers.ErrorsDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires de {@link RestAuthenticationEntryPoint}.
 */
class RestAuthenticationEntryPointTest {

    private final RestAuthenticationEntryPoint entryPoint =
            new RestAuthenticationEntryPoint(new ObjectMapper());

    @Test
    void commence_retourneUn401JsonAvecLeCodeBadCredentials() throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();

        entryPoint.commence(new MockHttpServletRequest(), response,
                new BadCredentialsException("Authentification requise"));

        assertEquals(401, response.getStatus());
        assertTrue(response.getContentType().startsWith("application/json"));

        ErrorsDto body = new ObjectMapper().readValue(response.getContentAsString(), ErrorsDto.class);
        assertEquals(401, body.getHttpCode());
        assertEquals(ErrorsCode.BAD_CREDENTIALS, body.getErrorsCode());
        assertEquals("Authentification requise", body.getMessage());
        assertFalse(body.getErrors().isEmpty());
    }
}
