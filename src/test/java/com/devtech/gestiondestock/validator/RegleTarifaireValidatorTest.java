package com.devtech.gestiondestock.validator;

import com.devtech.gestiondestock.dto.RegleTarifaireDto;
import com.devtech.gestiondestock.model.RegleTarifaire;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests unitaires du validateur de regle tarifaire.
 *
 * Champs obligatoires : code, nom, typeRegle, valeur, dateDebut.
 */
class RegleTarifaireValidatorTest {

    private RegleTarifaireDto.RegleTarifaireDtoBuilder validRegle() {
        return RegleTarifaireDto.builder()
                .code("RT-001")
                .nom("Remise commerciale")
                .typeRegle(RegleTarifaire.TypeRegle.REMISE_POURCENTAGE)
                .valeur(new BigDecimal("10"))
                .dateDebut(LocalDateTime.parse("2026-01-01T00:00:00"));
    }

    @Test
    @DisplayName("Regle null : les 5 messages obligatoires")
    void shouldRejectNullRegle() {
        List<String> errors = RegleTarifaireValidator.validate(null);

        assertFalse(errors.isEmpty());
        assertEquals(5, errors.size());
        assertEquals("Veuillez renseigner le code de la règle tarifaire", errors.get(0));
        assertEquals("Veuillez renseigner le nom de la règle tarifaire", errors.get(1));
        assertEquals("Veuillez renseigner le type de règle", errors.get(2));
        assertEquals("Veuillez renseigner la valeur de la règle", errors.get(3));
        assertEquals("Veuillez renseigner la date de début de la règle", errors.get(4));
    }

    @Test
    @DisplayName("Regle complete : aucune erreur")
    void shouldAcceptValidRegle() {
        assertTrue(RegleTarifaireValidator.validate(validRegle().build()).isEmpty());
    }

    @Test
    @DisplayName("Code vide : une seule erreur")
    void shouldRejectMissingCode() {
        List<String> errors = RegleTarifaireValidator.validate(validRegle().code("").build());

        assertEquals(1, errors.size());
        assertEquals("Veuillez renseigner le code de la règle tarifaire", errors.get(0));
    }

    @Test
    @DisplayName("Nom vide : une seule erreur")
    void shouldRejectMissingNom() {
        List<String> errors = RegleTarifaireValidator.validate(validRegle().nom("").build());

        assertEquals(1, errors.size());
        assertEquals("Veuillez renseigner le nom de la règle tarifaire", errors.get(0));
    }

    @Test
    @DisplayName("Type de regle absent : une seule erreur")
    void shouldRejectMissingTypeRegle() {
        List<String> errors = RegleTarifaireValidator.validate(validRegle().typeRegle(null).build());

        assertEquals(1, errors.size());
        assertEquals("Veuillez renseigner le type de règle", errors.get(0));
    }

    @Test
    @DisplayName("Valeur absente : une seule erreur")
    void shouldRejectMissingValeur() {
        List<String> errors = RegleTarifaireValidator.validate(validRegle().valeur(null).build());

        assertEquals(1, errors.size());
        assertEquals("Veuillez renseigner la valeur de la règle", errors.get(0));
    }

    @Test
    @DisplayName("Date de debut absente : une seule erreur")
    void shouldRejectMissingDateDebut() {
        List<String> errors = RegleTarifaireValidator.validate(validRegle().dateDebut(null).build());

        assertEquals(1, errors.size());
        assertEquals("Veuillez renseigner la date de début de la règle", errors.get(0));
    }
}
