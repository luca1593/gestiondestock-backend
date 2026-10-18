package com.devtech.gestiondestock.dto;

import com.devtech.gestiondestock.model.Adresse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Tests unitaires de la conversion DTO <-> entite pour {@link AdresseDto}.
 *
 * On verifie deux garanties : la tolerance au null (aucune NPE) et la
 * fidelite de la conversion dans les deux sens.
 */
class AdresseDtoTest {

    private AdresseDto sampleDto() {
        return AdresseDto.builder()
                .adresse1("12 rue de la Paix")
                .adresse2("Batiment B")
                .ville("Paris")
                .codePostal("75002")
                .pays("France")
                .build();
    }

    private Adresse sampleEntity() {
        Adresse adresse = new Adresse();
        adresse.setAdresse1("12 rue de la Paix");
        adresse.setAdresse2("Batiment B");
        adresse.setVille("Paris");
        adresse.setCodePostal("75002");
        adresse.setPays("France");
        return adresse;
    }

    @Nested
    @DisplayName("fromEntity")
    class FromEntity {

        @Test
        @DisplayName("null renvoie null (pas de NullPointerException)")
        void shouldReturnNullForNullEntity() {
            assertNull(AdresseDto.fromEntity(null));
        }

        @Test
        @DisplayName("copie tous les champs")
        void shouldMapAllFields() {
            AdresseDto dto = AdresseDto.fromEntity(sampleEntity());

            assertEquals("12 rue de la Paix", dto.getAdresse1());
            assertEquals("Batiment B", dto.getAdresse2());
            assertEquals("Paris", dto.getVille());
            assertEquals("75002", dto.getCodePostal());
            assertEquals("France", dto.getPays());
        }
    }

    @Nested
    @DisplayName("toEntity")
    class ToEntity {

        @Test
        @DisplayName("null renvoie null (pas de NullPointerException)")
        void shouldReturnNullForNullDto() {
            assertNull(AdresseDto.toEntity(null));
        }

        @Test
        @DisplayName("copie tous les champs")
        void shouldMapAllFields() {
            Adresse adresse = AdresseDto.toEntity(sampleDto());

            assertEquals("12 rue de la Paix", adresse.getAdresse1());
            assertEquals("Batiment B", adresse.getAdresse2());
            assertEquals("Paris", adresse.getVille());
            assertEquals("75002", adresse.getCodePostal());
            assertEquals("France", adresse.getPays());
        }
    }

    @Test
    @DisplayName("aller-retour DTO -> entite -> DTO conserve les donnees")
    void roundTripShouldPreserveData() {
        AdresseDto original = sampleDto();
        AdresseDto result = AdresseDto.fromEntity(AdresseDto.toEntity(original));

        assertEquals(original, result);
    }
}
