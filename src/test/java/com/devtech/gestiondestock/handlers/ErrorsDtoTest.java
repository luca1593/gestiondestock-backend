package com.devtech.gestiondestock.handlers;

import com.devtech.gestiondestock.exception.ErrorsCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests unitaires de l'objet de transport d'erreurs renvoye au frontend.
 */
class ErrorsDtoTest {

    @Test
    @DisplayName("La liste d'erreurs est initialisee par defaut (jamais null)")
    void defaultErrorsShouldNotBeNull() {
        ErrorsDto dto = new ErrorsDto();

        assertNotNull(dto.getErrors());
        assertTrue(dto.getErrors().isEmpty());
    }

    @Test
    @DisplayName("Le builder renseigne tous les champs")
    void builderShouldPopulateAllFields() {
        List<String> errors = List.of("erreur A", "erreur B");

        ErrorsDto dto = ErrorsDto.builder()
                .httpCode(400)
                .errorsCode(ErrorsCode.ARTICLE_NOT_VALID)
                .message("Article invalide")
                .errors(errors)
                .build();

        assertEquals(400, dto.getHttpCode());
        assertEquals(ErrorsCode.ARTICLE_NOT_VALID, dto.getErrorsCode());
        assertEquals("Article invalide", dto.getMessage());
        assertEquals(errors, dto.getErrors());
    }

    @Test
    @DisplayName("Les setters permettent de modifier chaque champ")
    void settersShouldUpdateFields() {
        ErrorsDto dto = new ErrorsDto();
        dto.setHttpCode(404);
        dto.setErrorsCode(ErrorsCode.ARTICLE_NOT_FOUND);
        dto.setMessage("introuvable");
        dto.setErrors(List.of("code"));

        assertEquals(404, dto.getHttpCode());
        assertEquals(ErrorsCode.ARTICLE_NOT_FOUND, dto.getErrorsCode());
        assertEquals("introuvable", dto.getMessage());
        assertEquals(1, dto.getErrors().size());
    }
}
