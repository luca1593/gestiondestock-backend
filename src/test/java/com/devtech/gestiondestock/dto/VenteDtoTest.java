package com.devtech.gestiondestock.dto;

import com.devtech.gestiondestock.model.Vente;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Tests unitaires de la conversion DTO <-> entite pour {@link VenteDto}.
 */
class VenteDtoTest {

    private Instant sampleDate() {
        return Instant.parse("2026-10-15T14:30:00Z");
    }

    private VenteDto sampleDto() {
        return VenteDto.builder()
                .id(11)
                .code("VTE-011")
                .dateVente(sampleDate())
                .commentaire("Vente comptoir")
                .identreprise(9)
                .build();
    }

    private Vente sampleEntity() {
        Vente vente = new Vente();
        vente.setId(11);
        vente.setCode("VTE-011");
        vente.setDateVente(sampleDate());
        vente.setCommentaire("Vente comptoir");
        vente.setIdentreprise(9);
        return vente;
    }

    @Nested
    @DisplayName("fromEntity")
    class FromEntity {

        @Test
        @DisplayName("null renvoie null (pas de NullPointerException)")
        void shouldReturnNullForNullEntity() {
            assertNull(VenteDto.fromEntity(null));
        }

        @Test
        @DisplayName("copie tous les champs simples")
        void shouldMapAllFields() {
            VenteDto dto = VenteDto.fromEntity(sampleEntity());

            assertEquals(Integer.valueOf(11), dto.getId());
            assertEquals("VTE-011", dto.getCode());
            assertEquals(sampleDate(), dto.getDateVente());
            assertEquals("Vente comptoir", dto.getCommentaire());
            assertEquals(Integer.valueOf(9), dto.getIdentreprise());
        }

        @Test
        @DisplayName("ne mappe pas la liste ligneVentes")
        void shouldNotMapLigneVentes() {
            assertNull(VenteDto.fromEntity(sampleEntity()).getLigneVentes());
        }
    }

    @Nested
    @DisplayName("toEntity")
    class ToEntity {

        @Test
        @DisplayName("null renvoie null (pas de NullPointerException)")
        void shouldReturnNullForNullDto() {
            assertNull(VenteDto.toEntity(null));
        }

        @Test
        @DisplayName("copie tous les champs simples")
        void shouldMapAllFields() {
            Vente vente = VenteDto.toEntity(sampleDto());

            assertEquals(Integer.valueOf(11), vente.getId());
            assertEquals("VTE-011", vente.getCode());
            assertEquals(sampleDate(), vente.getDateVente());
            assertEquals("Vente comptoir", vente.getCommentaire());
            assertEquals(Integer.valueOf(9), vente.getIdentreprise());
        }
    }

    @Test
    @DisplayName("aller-retour DTO -> entite -> DTO conserve les donnees")
    void roundTripShouldPreserveData() {
        VenteDto original = sampleDto();
        VenteDto result = VenteDto.fromEntity(VenteDto.toEntity(original));

        assertEquals(original, result);
    }
}
