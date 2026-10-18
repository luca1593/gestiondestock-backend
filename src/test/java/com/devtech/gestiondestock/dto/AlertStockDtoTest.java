package com.devtech.gestiondestock.dto;

import com.devtech.gestiondestock.model.AlertStock;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Tests unitaires de la conversion entite -> DTO pour {@link AlertStockDto}.
 *
 * AlertStockDto ne propose pas de methode toEntity : on verifie donc
 * uniquement la tolerance au null et la fidelite de fromEntity.
 */
class AlertStockDtoTest {

    private AlertStock sampleEntity() {
        AlertStock alert = new AlertStock();
        alert.setId(7);
        alert.setArticleId(42);
        alert.setDesignation("Cafe en grains");
        alert.setStockActuel(3.5);
        alert.setSeuilMinimum(10.0);
        alert.setSeuilCritique(5.0);
        alert.setNiveauAlerte("CRITIQUE");
        alert.setActive(Boolean.TRUE);
        alert.setIdentreprise(1);
        return alert;
    }

    @Nested
    @DisplayName("fromEntity")
    class FromEntity {

        @Test
        @DisplayName("null renvoie null (pas de NullPointerException)")
        void shouldReturnNullForNullEntity() {
            assertNull(AlertStockDto.fromEntity(null));
        }

        @Test
        @DisplayName("copie tous les champs")
        void shouldMapAllFields() {
            AlertStockDto dto = AlertStockDto.fromEntity(sampleEntity());

            assertEquals(Integer.valueOf(7), dto.getId());
            assertEquals(Integer.valueOf(42), dto.getArticleId());
            assertEquals("Cafe en grains", dto.getDesignation());
            assertEquals(Double.valueOf(3.5), dto.getStockActuel());
            assertEquals(Double.valueOf(10.0), dto.getSeuilMinimum());
            assertEquals(Double.valueOf(5.0), dto.getSeuilCritique());
            assertEquals("CRITIQUE", dto.getNiveauAlerte());
            assertEquals(Boolean.TRUE, dto.getActive());
            assertEquals(Integer.valueOf(1), dto.getIdentreprise());
        }
    }
}
