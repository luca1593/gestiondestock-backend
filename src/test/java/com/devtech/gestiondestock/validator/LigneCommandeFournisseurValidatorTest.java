package com.devtech.gestiondestock.validator;

import com.devtech.gestiondestock.dto.LigneCommandeFournisseurDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests unitaires du validateur de ligne de commande fournisseur.
 *
 * Seule la quantite est verifiee : elle doit etre strictement positive.
 * La regle reelle est quantite.compareTo(BigDecimal.ZERO) < 1, soit quantite <= 0.
 */
class LigneCommandeFournisseurValidatorTest {

    @Test
    @DisplayName("Ligne null : une erreur sur la quantite")
    void shouldRejectNullLigne() {
        List<String> errors = LigneCommandeFournisseurValidator.validate(null);

        assertFalse(errors.isEmpty());
        assertEquals(1, errors.size());
        assertEquals("Veuillez renseigner la quantite de la commande", errors.get(0));
    }

    @Test
    @DisplayName("Quantite positive : aucune erreur")
    void shouldAcceptPositiveQuantite() {
        assertTrue(LigneCommandeFournisseurValidator.validate(
                LigneCommandeFournisseurDto.builder().quantite(BigDecimal.ONE).build()).isEmpty());
    }

    @Test
    @DisplayName("Quantite nulle : une erreur")
    void shouldRejectZeroQuantite() {
        List<String> errors = LigneCommandeFournisseurValidator.validate(
                LigneCommandeFournisseurDto.builder().quantite(BigDecimal.ZERO).build());

        assertEquals(1, errors.size());
        assertEquals("Veuillez renseigner la quantite de la commande", errors.get(0));
    }

    @Test
    @DisplayName("Quantite negative : une erreur")
    void shouldRejectNegativeQuantite() {
        List<String> errors = LigneCommandeFournisseurValidator.validate(
                LigneCommandeFournisseurDto.builder().quantite(new BigDecimal("-1")).build());

        assertEquals(1, errors.size());
        assertEquals("Veuillez renseigner la quantite de la commande", errors.get(0));
    }

    @Test
    @DisplayName("Quantite absente : une erreur")
    void shouldRejectNullQuantite() {
        List<String> errors = LigneCommandeFournisseurValidator.validate(
                LigneCommandeFournisseurDto.builder().quantite(null).build());

        assertEquals(1, errors.size());
        assertEquals("Veuillez renseigner la quantite de la commande", errors.get(0));
    }
}
