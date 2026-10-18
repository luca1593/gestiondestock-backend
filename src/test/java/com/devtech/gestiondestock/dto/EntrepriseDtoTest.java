package com.devtech.gestiondestock.dto;

import com.devtech.gestiondestock.model.Adresse;
import com.devtech.gestiondestock.model.Entreprise;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Tests unitaires de la conversion DTO <-> entite pour {@link EntrepriseDto}.
 */
class EntrepriseDtoTest {

    private AdresseDto sampleAdresseDto() {
        return AdresseDto.builder()
                .adresse1("10 rue de la Societe")
                .ville("Paris")
                .codePostal("75008")
                .pays("France")
                .build();
    }

    private Adresse sampleAdresse() {
        Adresse adresse = new Adresse();
        adresse.setAdresse1("10 rue de la Societe");
        adresse.setVille("Paris");
        adresse.setCodePostal("75008");
        adresse.setPays("France");
        return adresse;
    }

    private EntrepriseDto sampleDto() {
        return EntrepriseDto.builder()
                .id(9)
                .nom("Ma societe")
                .description("Distribution de produits")
                .adresse(sampleAdresseDto())
                .codeFiscal("FR123456789")
                .photo("logo.png")
                .email("contact@masociete.com")
                .numTel("0140506070")
                .siteWeb("https://masociete.com")
                .build();
    }

    private Entreprise sampleEntity() {
        Entreprise entreprise = new Entreprise();
        entreprise.setId(9);
        entreprise.setNom("Ma societe");
        entreprise.setDescription("Distribution de produits");
        entreprise.setAdresse(sampleAdresse());
        entreprise.setCodeFiscal("FR123456789");
        entreprise.setPhoto("logo.png");
        entreprise.setEmail("contact@masociete.com");
        entreprise.setNumTel("0140506070");
        entreprise.setSiteWeb("https://masociete.com");
        return entreprise;
    }

    @Nested
    @DisplayName("fromEntity")
    class FromEntity {

        @Test
        @DisplayName("null renvoie null (pas de NullPointerException)")
        void shouldReturnNullForNullEntity() {
            assertNull(EntrepriseDto.fromEntity(null));
        }

        @Test
        @DisplayName("copie tous les champs et imbrique l'adresse")
        void shouldMapAllFields() {
            EntrepriseDto dto = EntrepriseDto.fromEntity(sampleEntity());

            assertEquals(Integer.valueOf(9), dto.getId());
            assertEquals("Ma societe", dto.getNom());
            assertEquals("Distribution de produits", dto.getDescription());
            assertEquals("FR123456789", dto.getCodeFiscal());
            assertEquals("logo.png", dto.getPhoto());
            assertEquals("contact@masociete.com", dto.getEmail());
            assertEquals("0140506070", dto.getNumTel());
            assertEquals("https://masociete.com", dto.getSiteWeb());
            assertNotNull(dto.getAdresse());
            assertEquals("Paris", dto.getAdresse().getVille());
        }

        @Test
        @DisplayName("ne mappe pas la liste utilisateurs")
        void shouldNotMapUtilisateurs() {
            assertNull(EntrepriseDto.fromEntity(sampleEntity()).getUtilisateurs());
        }
    }

    @Nested
    @DisplayName("toEntity")
    class ToEntity {

        @Test
        @DisplayName("null renvoie null (pas de NullPointerException)")
        void shouldReturnNullForNullDto() {
            assertNull(EntrepriseDto.toEntity(null));
        }

        @Test
        @DisplayName("copie tous les champs et imbrique l'adresse")
        void shouldMapAllFields() {
            Entreprise entreprise = EntrepriseDto.toEntity(sampleDto());

            assertEquals(Integer.valueOf(9), entreprise.getId());
            assertEquals("Ma societe", entreprise.getNom());
            assertEquals("Distribution de produits", entreprise.getDescription());
            assertEquals("FR123456789", entreprise.getCodeFiscal());
            assertEquals("logo.png", entreprise.getPhoto());
            assertEquals("contact@masociete.com", entreprise.getEmail());
            assertEquals("0140506070", entreprise.getNumTel());
            assertEquals("https://masociete.com", entreprise.getSiteWeb());
            assertNotNull(entreprise.getAdresse());
            assertEquals("Paris", entreprise.getAdresse().getVille());
        }
    }

    @Test
    @DisplayName("aller-retour DTO -> entite -> DTO conserve les donnees")
    void roundTripShouldPreserveData() {
        EntrepriseDto original = sampleDto();
        EntrepriseDto result = EntrepriseDto.fromEntity(EntrepriseDto.toEntity(original));

        assertEquals(original, result);
    }
}
