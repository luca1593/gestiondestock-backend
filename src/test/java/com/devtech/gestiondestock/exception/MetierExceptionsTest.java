package com.devtech.gestiondestock.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * Tests unitaires des exceptions metier.
 *
 * Verifie que le message, la cause et le code d'erreur sont correctement
 * transportes par les differents constructeurs.
 */
class MetierExceptionsTest {

    @Test
    @DisplayName("EntityNotFoundException transporte message, cause et code d'erreur")
    void entityNotFoundExceptionShouldExposeItsState() {
        EntityNotFoundException withCode =
                new EntityNotFoundException("absent", ErrorsCode.ARTICLE_NOT_FOUND);
        assertEquals("absent", withCode.getMessage());
        assertSame(ErrorsCode.ARTICLE_NOT_FOUND, withCode.getErrorsCode());

        RuntimeException cause = new RuntimeException("cause");
        EntityNotFoundException withCause =
                new EntityNotFoundException("absent", cause, ErrorsCode.CLIENT_NOT_FOUND);
        assertSame(cause, withCause.getCause());
        assertSame(ErrorsCode.CLIENT_NOT_FOUND, withCause.getErrorsCode());

        assertNull(new EntityNotFoundException("simple").getErrorsCode());
    }

    @Test
    @DisplayName("InvalidEntityException transporte la liste des erreurs de validation")
    void invalidEntityExceptionShouldExposeErrors() {
        List<String> errors = List.of("erreur 1", "erreur 2");
        InvalidEntityException exception =
                new InvalidEntityException("invalide", ErrorsCode.ARTICLE_NOT_VALID, errors);

        assertEquals("invalide", exception.getMessage());
        assertSame(ErrorsCode.ARTICLE_NOT_VALID, exception.getErrorsCode());
        assertEquals(errors, exception.getErrors());
        assertNull(new InvalidEntityException("simple").getErrors());
    }

    @Test
    @DisplayName("InvalidOpperatioException transporte le code d'erreur")
    void invalidOperationExceptionShouldExposeCode() {
        InvalidOpperatioException exception =
                new InvalidOpperatioException("impossible", ErrorsCode.COMMANDE_CLIENT_NON_MODIFIABLE);

        assertEquals("impossible", exception.getMessage());
        assertSame(ErrorsCode.COMMANDE_CLIENT_NON_MODIFIABLE, exception.getErrorsCode());
    }

    @Test
    @DisplayName("Chaque code d'erreur metier possede un entier unique non nul")
    void errorCodesShouldBeUnique() {
        ErrorsCode[] values = ErrorsCode.values();
        java.util.Set<Integer> codes = new java.util.HashSet<>();
        for (ErrorsCode value : values) {
            org.junit.jupiter.api.Assertions.assertTrue(value.getCode() > 0,
                    "Le code de " + value.name() + " doit etre positif");
            org.junit.jupiter.api.Assertions.assertTrue(codes.add(value.getCode()),
                    "Le code " + value.getCode() + " est duplique (" + value.name() + ")");
        }
    }
}
