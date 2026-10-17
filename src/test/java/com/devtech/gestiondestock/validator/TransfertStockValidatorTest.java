package com.devtech.gestiondestock.validator;

import com.devtech.gestiondestock.dto.TransfertStockDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests unitaires du validateur de transfert de stock.
 *
 * Champs obligatoires : code, dateTransfert, entrepotSourceId, entrepotDestinationId.
 * Les entrepots source et destination doivent etre differents.
 */
class TransfertStockValidatorTest {

    private TransfertStockDto.TransfertStockDtoBuilder validTransfert() {
        return TransfertStockDto.builder()
                .code("TR-001")
                .dateTransfert(LocalDateTime.parse("2026-01-01T10:00:00"))
                .entrepotSourceId(1)
                .entrepotDestinationId(2);
    }

    @Test
    @DisplayName("Transfert null : les 4 messages obligatoires")
    void shouldRejectNullTransfert() {
        List<String> errors = TransfertStockValidator.validate(null);

        assertFalse(errors.isEmpty());
        assertEquals(4, errors.size());
        assertEquals("Veuillez renseigner le code du transfert", errors.get(0));
        assertEquals("Veuillez renseigner la date du transfert", errors.get(1));
        assertEquals("Veuillez selectionner un entrepôt source", errors.get(2));
        assertEquals("Veuillez selectionner un entrepôt de destination", errors.get(3));
    }

    @Test
    @DisplayName("Transfert complet : aucune erreur")
    void shouldAcceptValidTransfert() {
        assertTrue(TransfertStockValidator.validate(validTransfert().build()).isEmpty());
    }

    @Test
    @DisplayName("Code vide : une seule erreur")
    void shouldRejectMissingCode() {
        List<String> errors = TransfertStockValidator.validate(validTransfert().code("").build());

        assertEquals(1, errors.size());
        assertEquals("Veuillez renseigner le code du transfert", errors.get(0));
    }

    @Test
    @DisplayName("Date absente : une seule erreur")
    void shouldRejectMissingDate() {
        List<String> errors = TransfertStockValidator.validate(validTransfert().dateTransfert(null).build());

        assertEquals(1, errors.size());
        assertEquals("Veuillez renseigner la date du transfert", errors.get(0));
    }

    @Test
    @DisplayName("Entrepot source absent : une seule erreur")
    void shouldRejectMissingEntrepotSource() {
        List<String> errors = TransfertStockValidator.validate(validTransfert().entrepotSourceId(null).build());

        assertEquals(1, errors.size());
        assertEquals("Veuillez selectionner un entrepôt source", errors.get(0));
    }

    @Test
    @DisplayName("Entrepot de destination absent : une seule erreur")
    void shouldRejectMissingEntrepotDestination() {
        List<String> errors = TransfertStockValidator.validate(validTransfert().entrepotDestinationId(null).build());

        assertEquals(1, errors.size());
        assertEquals("Veuillez selectionner un entrepôt de destination", errors.get(0));
    }

    @Test
    @DisplayName("Entrepot source egal a la destination : une seule erreur")
    void shouldRejectSameEntrepots() {
        List<String> errors = TransfertStockValidator.validate(
                validTransfert().entrepotDestinationId(1).build());

        assertEquals(1, errors.size());
        assertEquals("L'entrepôt source et l'entrepôt de destination doivent être différents", errors.get(0));
    }
}
