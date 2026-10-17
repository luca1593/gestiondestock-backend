package com.devtech.gestiondestock.validator;

import com.devtech.gestiondestock.dto.CategoryDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests unitaires du validateur de categorie.
 *
 * Seul le code est obligatoire ; la designation n'est pas verifiee.
 */
class CategoryValidatorTest {

    @Test
    @DisplayName("Categorie null : une erreur sur le code")
    void shouldRejectNullCategory() {
        List<String> errors = CategoryValidator.validate(null);

        assertFalse(errors.isEmpty());
        assertEquals(1, errors.size());
        assertEquals("Veuillez renseigner le code de la categorie", errors.get(0));
    }

    @Test
    @DisplayName("Categorie avec code : aucune erreur")
    void shouldAcceptValidCategory() {
        assertTrue(CategoryValidator.validate(CategoryDto.builder().code("CAT-001").build()).isEmpty());
    }

    @Test
    @DisplayName("Code vide : une erreur")
    void shouldRejectMissingCode() {
        List<String> errors = CategoryValidator.validate(CategoryDto.builder().code("").build());

        assertEquals(1, errors.size());
        assertEquals("Veuillez renseigner le code de la categorie", errors.get(0));
    }

    @Test
    @DisplayName("Designation absente : aucune erreur (champ non verifie)")
    void shouldAcceptMissingDesignation() {
        assertTrue(CategoryValidator.validate(CategoryDto.builder().code("CAT-001").build()).isEmpty());
    }
}
