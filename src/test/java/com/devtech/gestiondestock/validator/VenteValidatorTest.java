package com.devtech.gestiondestock.validator;

import com.devtech.gestiondestock.dto.LigneVenteDto;
import com.devtech.gestiondestock.dto.VenteDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests unitaires du validateur de vente.
 *
 * Champs obligatoires : code, dateVente et au moins une ligne de vente.
 */
class VenteValidatorTest {

    private VenteDto.VenteDtoBuilder validVente() {
        return VenteDto.builder()
                .code("VT-001")
                .dateVente(Instant.parse("2026-01-01T10:00:00Z"))
                .ligneVentes(Collections.singletonList(
                        LigneVenteDto.builder().quantite(BigDecimal.ONE).build()));
    }

    @Test
    @DisplayName("Vente null : les 3 messages obligatoires")
    void shouldRejectNullVente() {
        List<String> errors = VenteValidator.validate(null);

        assertFalse(errors.isEmpty());
        assertEquals(3, errors.size());
        assertEquals("Veuillez renseigner le code de la vente", errors.get(0));
        assertEquals("Veuillez renseigner la date de la vente", errors.get(1));
        assertEquals("Veuillez ajouter au moins un article pour la vente", errors.get(2));
    }

    @Test
    @DisplayName("Vente complete : aucune erreur")
    void shouldAcceptValidVente() {
        assertTrue(VenteValidator.validate(validVente().build()).isEmpty());
    }

    @Test
    @DisplayName("Code vide : une seule erreur")
    void shouldRejectMissingCode() {
        List<String> errors = VenteValidator.validate(validVente().code("").build());

        assertEquals(1, errors.size());
        assertEquals("Veuillez renseigner le code de la vente", errors.get(0));
    }

    @Test
    @DisplayName("Date absente : une seule erreur")
    void shouldRejectMissingDate() {
        List<String> errors = VenteValidator.validate(validVente().dateVente(null).build());

        assertEquals(1, errors.size());
        assertEquals("Veuillez renseigner la date de la vente", errors.get(0));
    }

    @Test
    @DisplayName("Aucune ligne de vente : une seule erreur")
    void shouldRejectEmptyLignes() {
        List<String> errors = VenteValidator.validate(validVente().ligneVentes(Collections.emptyList()).build());

        assertEquals(1, errors.size());
        assertEquals("Veuillez ajouter au moins un article pour la vente", errors.get(0));
    }

    @Test
    @DisplayName("Liste de lignes nulle : une seule erreur")
    void shouldRejectNullLignes() {
        List<String> errors = VenteValidator.validate(validVente().ligneVentes(null).build());

        assertEquals(1, errors.size());
        assertEquals("Veuillez ajouter au moins un article pour la vente", errors.get(0));
    }
}
