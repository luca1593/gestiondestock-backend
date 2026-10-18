package com.devtech.gestiondestock.validator;

import com.devtech.gestiondestock.dto.LotDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests unitaires du validateur de lot.
 *
 * Champs obligatoires : numeroLot, quantite, prixAchat, articleId, entrepotId.
 */
class LotValidatorTest {

    private LotDto.LotDtoBuilder validLot() {
        return LotDto.builder()
                .numeroLot("LOT-001")
                .quantite(new BigDecimal("50"))
                .prixAchat(new BigDecimal("10.00"))
                .articleId(1)
                .entrepotId(1);
    }

    @Test
    @DisplayName("Lot null : les 5 messages obligatoires")
    void shouldRejectNullLot() {
        List<String> errors = LotValidator.validate(null);

        assertFalse(errors.isEmpty());
        assertEquals(5, errors.size());
        assertEquals("Veuillez renseigner le numéro du lot", errors.get(0));
        assertEquals("Veuillez renseigner la quantité du lot", errors.get(1));
        assertEquals("Veuillez renseigner le prix d'achat du lot", errors.get(2));
        assertEquals("Veuillez selectionner un article pour le lot", errors.get(3));
        assertEquals("Veuillez selectionner un entrepôt pour le lot", errors.get(4));
    }

    @Test
    @DisplayName("Lot complet : aucune erreur")
    void shouldAcceptValidLot() {
        assertTrue(LotValidator.validate(validLot().build()).isEmpty());
    }

    @Test
    @DisplayName("Numero de lot vide : une seule erreur")
    void shouldRejectMissingNumeroLot() {
        List<String> errors = LotValidator.validate(validLot().numeroLot("").build());

        assertEquals(1, errors.size());
        assertEquals("Veuillez renseigner le numéro du lot", errors.get(0));
    }

    @Test
    @DisplayName("Quantite absente : une seule erreur")
    void shouldRejectMissingQuantite() {
        List<String> errors = LotValidator.validate(validLot().quantite(null).build());

        assertEquals(1, errors.size());
        assertEquals("Veuillez renseigner la quantité du lot", errors.get(0));
    }

    @Test
    @DisplayName("Prix d'achat absent : une seule erreur")
    void shouldRejectMissingPrixAchat() {
        List<String> errors = LotValidator.validate(validLot().prixAchat(null).build());

        assertEquals(1, errors.size());
        assertEquals("Veuillez renseigner le prix d'achat du lot", errors.get(0));
    }

    @Test
    @DisplayName("Article absent : une seule erreur")
    void shouldRejectMissingArticle() {
        List<String> errors = LotValidator.validate(validLot().articleId(null).build());

        assertEquals(1, errors.size());
        assertEquals("Veuillez selectionner un article pour le lot", errors.get(0));
    }

    @Test
    @DisplayName("Entrepot absent : une seule erreur")
    void shouldRejectMissingEntrepot() {
        List<String> errors = LotValidator.validate(validLot().entrepotId(null).build());

        assertEquals(1, errors.size());
        assertEquals("Veuillez selectionner un entrepôt pour le lot", errors.get(0));
    }

    @Test
    @DisplayName("Quantite negative : une erreur (M-3 corrige)")
    void shouldRejectNegativeQuantite() {
        List<String> errors = LotValidator.validate(validLot().quantite(new BigDecimal("-5")).build());

        assertEquals(1, errors.size());
        assertEquals("La quantité du lot ne peut pas être négative", errors.get(0));
    }
}
