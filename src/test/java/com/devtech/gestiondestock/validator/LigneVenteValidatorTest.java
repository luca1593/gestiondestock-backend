package com.devtech.gestiondestock.validator;

import com.devtech.gestiondestock.dto.LigneVenteDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests unitaires du validateur de ligne de vente.
 *
 * Seule la quantite est verifiee, et uniquement sa presence : contrairement aux
 * lignes de commande, aucune contrainte de positivite n'est appliquee ici.
 */
class LigneVenteValidatorTest {

    @Test
    @DisplayName("Ligne null : une erreur sur la quantite")
    void shouldRejectNullLigne() {
        List<String> errors = LigneVenteValidator.validate(null);

        assertFalse(errors.isEmpty());
        assertEquals(1, errors.size());
        assertEquals("Veuillez renseigner la quantite de la commande", errors.get(0));
    }

    @Test
    @DisplayName("Quantite positive : aucune erreur")
    void shouldAcceptPositiveQuantite() {
        assertTrue(LigneVenteValidator.validate(
                LigneVenteDto.builder().quantite(BigDecimal.ONE).build()).isEmpty());
    }

    @Test
    @DisplayName("Quantite absente : une erreur")
    void shouldRejectNullQuantite() {
        List<String> errors = LigneVenteValidator.validate(
                LigneVenteDto.builder().quantite(null).build());

        assertEquals(1, errors.size());
        assertEquals("Veuillez renseigner la quantite de la commande", errors.get(0));
    }

    @Test
    @DisplayName("Quantite nulle : acceptee (aucun controle de positivite)")
    void shouldAcceptZeroQuantite() {
        assertTrue(LigneVenteValidator.validate(
                LigneVenteDto.builder().quantite(BigDecimal.ZERO).build()).isEmpty());
    }

    @Test
    @DisplayName("Quantite negative : acceptee (aucun controle de positivite)")
    void shouldAcceptNegativeQuantite() {
        assertTrue(LigneVenteValidator.validate(
                LigneVenteDto.builder().quantite(new BigDecimal("-1")).build()).isEmpty());
    }
}
