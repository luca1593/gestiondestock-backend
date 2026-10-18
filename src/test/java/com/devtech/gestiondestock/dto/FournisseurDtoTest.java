package com.devtech.gestiondestock.dto;

import com.devtech.gestiondestock.model.Adresse;
import com.devtech.gestiondestock.model.Fournisseur;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Tests unitaires de la conversion DTO <-> entite pour {@link FournisseurDto}.
 */
class FournisseurDtoTest {

    private AdresseDto sampleAdresseDto() {
        return AdresseDto.builder()
                .adresse1("5 avenue B")
                .ville("Marseille")
                .codePostal("13001")
                .pays("France")
                .build();
    }

    private Adresse sampleAdresse() {
        Adresse adresse = new Adresse();
        adresse.setAdresse1("5 avenue B");
        adresse.setVille("Marseille");
        adresse.setCodePostal("13001");
        adresse.setPays("France");
        return adresse;
    }

    private FournisseurDto sampleDto() {
        return FournisseurDto.builder()
                .id(8)
                .nom("Martin")
                .prenom("Paul")
                .adresse(sampleAdresseDto())
                .photo("fournisseur.png")
                .email("paul.martin@example.com")
                .numTel("0607080910")
                .identreprise(9)
                .build();
    }

    private Fournisseur sampleEntity() {
        Fournisseur fournisseur = new Fournisseur();
        fournisseur.setId(8);
        fournisseur.setNom("Martin");
        fournisseur.setPrenom("Paul");
        fournisseur.setAdresse(sampleAdresse());
        fournisseur.setPhoto("fournisseur.png");
        fournisseur.setEmail("paul.martin@example.com");
        fournisseur.setNumTel("0607080910");
        fournisseur.setIdentreprise(9);
        return fournisseur;
    }

    @Nested
    @DisplayName("fromEntity")
    class FromEntity {

        @Test
        @DisplayName("null renvoie null (pas de NullPointerException)")
        void shouldReturnNullForNullEntity() {
            assertNull(FournisseurDto.fromEntity(null));
        }

        @Test
        @DisplayName("copie tous les champs et imbrique l'adresse")
        void shouldMapAllFields() {
            FournisseurDto dto = FournisseurDto.fromEntity(sampleEntity());

            assertEquals(Integer.valueOf(8), dto.getId());
            assertEquals("Martin", dto.getNom());
            assertEquals("Paul", dto.getPrenom());
            assertEquals("fournisseur.png", dto.getPhoto());
            assertEquals("paul.martin@example.com", dto.getEmail());
            assertEquals("0607080910", dto.getNumTel());
            assertEquals(Integer.valueOf(9), dto.getIdentreprise());
            assertNotNull(dto.getAdresse());
            assertEquals("Marseille", dto.getAdresse().getVille());
        }

        @Test
        @DisplayName("ne mappe pas la liste commandeFournisseurs")
        void shouldNotMapCommandes() {
            assertNull(FournisseurDto.fromEntity(sampleEntity()).getCommandeFournisseurs());
        }
    }

    @Nested
    @DisplayName("toEntity")
    class ToEntity {

        @Test
        @DisplayName("null renvoie null (pas de NullPointerException)")
        void shouldReturnNullForNullDto() {
            assertNull(FournisseurDto.toEntity(null));
        }

        @Test
        @DisplayName("copie tous les champs et imbrique l'adresse")
        void shouldMapAllFields() {
            Fournisseur fournisseur = FournisseurDto.toEntity(sampleDto());

            assertEquals(Integer.valueOf(8), fournisseur.getId());
            assertEquals("Martin", fournisseur.getNom());
            assertEquals("Paul", fournisseur.getPrenom());
            assertEquals("fournisseur.png", fournisseur.getPhoto());
            assertEquals("paul.martin@example.com", fournisseur.getEmail());
            assertEquals("0607080910", fournisseur.getNumTel());
            assertEquals(Integer.valueOf(9), fournisseur.getIdentreprise());
            assertNotNull(fournisseur.getAdresse());
            assertEquals("Marseille", fournisseur.getAdresse().getVille());
        }
    }

    @Test
    @DisplayName("aller-retour DTO -> entite -> DTO conserve les donnees")
    void roundTripShouldPreserveData() {
        FournisseurDto original = sampleDto();
        FournisseurDto result = FournisseurDto.fromEntity(FournisseurDto.toEntity(original));

        assertEquals(original, result);
    }
}
