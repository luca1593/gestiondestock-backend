package com.devtech.gestiondestock.validator;

import com.devtech.gestiondestock.dto.InventaireDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests unitaires du validateur d'inventaire.
 *
 * Champs obligatoires : code, dateDebut, entrepotId.
 */
class InventaireValidatorTest {

    private InventaireDto.InventaireDtoBuilder validInventaire() {
        return InventaireDto.builder()
                .code("INV-001")
                .dateDebut(LocalDateTime.parse("2026-01-01T09:00:00"))
                .entrepotId(1);
    }

    @Test
    @DisplayName("Inventaire null : les 3 messages obligatoires")
    void shouldRejectNullInventaire() {
        List<String> errors = InventaireValidator.validate(null);

        assertFalse(errors.isEmpty());
        assertEquals(3, errors.size());
        assertEquals("Veuillez renseigner le code de l'inventaire", errors.get(0));
        assertEquals("Veuillez renseigner la date de début de l'inventaire", errors.get(1));
        assertEquals("Veuillez selectionner un entrepôt pour l'inventaire", errors.get(2));
    }

    @Test
    @DisplayName("Inventaire complet : aucune erreur")
    void shouldAcceptValidInventaire() {
        assertTrue(InventaireValidator.validate(validInventaire().build()).isEmpty());
    }

    @Test
    @DisplayName("Code vide : une seule erreur")
    void shouldRejectMissingCode() {
        List<String> errors = InventaireValidator.validate(validInventaire().code("").build());

        assertEquals(1, errors.size());
        assertEquals("Veuillez renseigner le code de l'inventaire", errors.get(0));
    }

    @Test
    @DisplayName("Date de debut absente : une seule erreur")
    void shouldRejectMissingDateDebut() {
        List<String> errors = InventaireValidator.validate(validInventaire().dateDebut(null).build());

        assertEquals(1, errors.size());
        assertEquals("Veuillez renseigner la date de début de l'inventaire", errors.get(0));
    }

    @Test
    @DisplayName("Entrepot absent : une seule erreur")
    void shouldRejectMissingEntrepot() {
        List<String> errors = InventaireValidator.validate(validInventaire().entrepotId(null).build());

        assertEquals(1, errors.size());
        assertEquals("Veuillez selectionner un entrepôt pour l'inventaire", errors.get(0));
    }
}
