package com.devtech.gestiondestock.handlers;

import com.devtech.gestiondestock.exception.EntityNotFoundException;
import com.devtech.gestiondestock.exception.ErrorsCode;
import com.devtech.gestiondestock.exception.InvalidEntityException;
import com.devtech.gestiondestock.exception.InvalidOpperatioException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests unitaires du gestionnaire global d'exceptions.
 *
 * On verifie le contrat HTTP expose au frontend : chaque exception metier
 * doit produire le bon code HTTP et le bon {@link ErrorsDto}.
 */
class RestExceptionHandlerTest {

    private final RestExceptionHandler handler = new RestExceptionHandler();

    @Test
    @DisplayName("EntityNotFoundException -> 404 NOT_FOUND avec le code d'erreur metier")
    void shouldMapEntityNotFoundTo404() {
        EntityNotFoundException exception =
                new EntityNotFoundException("Article introuvable", ErrorsCode.ARTICLE_NOT_FOUND);

        ResponseEntity<ErrorsDto> response = handler.handleException(exception, null);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        ErrorsDto body = response.getBody();
        assertNotNull(body);
        assertEquals(404, body.getHttpCode());
        assertEquals(ErrorsCode.ARTICLE_NOT_FOUND, body.getErrorsCode());
        assertEquals("Article introuvable", body.getMessage());
    }

    @Test
    @DisplayName("InvalidEntityException -> 400 BAD_REQUEST avec la liste des erreurs de validation")
    void shouldMapInvalidEntityTo400() {
        List<String> errors = List.of("Le code est obligatoire", "La designation est obligatoire");
        InvalidEntityException exception = new InvalidEntityException(
                "Article invalide", ErrorsCode.ARTICLE_NOT_VALID, errors);

        ResponseEntity<ErrorsDto> response = handler.handleException(exception, null);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ErrorsDto body = response.getBody();
        assertNotNull(body);
        assertEquals(400, body.getHttpCode());
        assertEquals(ErrorsCode.ARTICLE_NOT_VALID, body.getErrorsCode());
        assertEquals(2, body.getErrors().size());
        assertTrue(body.getErrors().contains("Le code est obligatoire"));
    }

    @Test
    @DisplayName("InvalidOpperatioException -> 400 BAD_REQUEST")
    void shouldMapInvalidOperationTo400() {
        InvalidOpperatioException exception = new InvalidOpperatioException(
                "Operation impossible", ErrorsCode.COMMANDE_CLIENT_NON_MODIFIABLE);

        ResponseEntity<ErrorsDto> response = handler.handleException(exception, null);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(ErrorsCode.COMMANDE_CLIENT_NON_MODIFIABLE, response.getBody().getErrorsCode());
    }

    @Test
    @DisplayName("BadCredentialsException -> 400 avec le message generique d'authentification")
    void shouldMapBadCredentialsTo400() {
        BadCredentialsException exception = new BadCredentialsException("Bad credentials");

        ResponseEntity<ErrorsDto> response = handler.handleException(exception, null);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ErrorsDto body = response.getBody();
        assertNotNull(body);
        assertEquals(ErrorsCode.BAD_CREDENTIALS, body.getErrorsCode());
        assertEquals(1, body.getErrors().size());
        assertEquals("Login et / ou mot de passe incorrecte", body.getErrors().get(0));
    }

    @Test
    @DisplayName("DataIntegrityViolationException -> 400 (et non 500) avec un message exploitable")
    void shouldMapDataIntegrityViolationTo400() {
        DataIntegrityViolationException exception = new DataIntegrityViolationException(
                "could not execute statement",
                new SQLIntegrityConstraintViolationException(
                        "Cannot delete or update a parent row: a foreign key constraint fails"));

        ResponseEntity<ErrorsDto> response = handler.handleException(exception, null);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ErrorsDto body = response.getBody();
        assertNotNull(body);
        assertEquals(400, body.getHttpCode());
        assertEquals(ErrorsCode.DATA_INTEGRITY_VIOLATION, body.getErrorsCode());
        assertNotNull(body.getMessage());
    }
}
