package com.devtech.gestiondestock.dto;

import com.devtech.gestiondestock.model.Category;
import com.devtech.gestiondestock.model.Client;
import com.devtech.gestiondestock.model.Entreprise;
import com.devtech.gestiondestock.model.RegleTarifaire;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Tests unitaires de la conversion DTO <-> entite pour {@link RegleTarifaireDto}.
 *
 * Particularite : fromEntity derive entreprise / categorie / client sous forme
 * d'identifiants et de libelles, mais toEntity ne reconstruit pas ces associations.
 */
class RegleTarifaireDtoTest {

    private LocalDateTime sampleDebut() {
        return LocalDateTime.of(2026, 1, 1, 0, 0);
    }

    private LocalDateTime sampleFin() {
        return LocalDateTime.of(2026, 12, 31, 23, 59);
    }

    private Entreprise sampleEntreprise() {
        Entreprise entreprise = new Entreprise();
        entreprise.setId(9);
        entreprise.setNom("Ma societe");
        return entreprise;
    }

    private Category sampleCategorie() {
        Category categorie = new Category();
        categorie.setId(2);
        categorie.setDesignation("Boissons");
        return categorie;
    }

    private Client sampleClient() {
        Client client = new Client();
        client.setId(3);
        client.setNom("Dupont");
        return client;
    }

    private RegleTarifaireDto sampleDto() {
        return RegleTarifaireDto.builder()
                .id(500)
                .code("REG-500")
                .nom("Remise fideles")
                .description("Remise de 10%")
                .typeRegle(RegleTarifaire.TypeRegle.REMISE_POURCENTAGE)
                .valeur(new BigDecimal("10"))
                .estActif(Boolean.TRUE)
                .dateDebut(sampleDebut())
                .dateFin(sampleFin())
                .quantiteMinimale(new BigDecimal("5"))
                .montantMinimal(new BigDecimal("50.00"))
                .build();
    }

    private RegleTarifaire sampleEntity() {
        RegleTarifaire regle = new RegleTarifaire();
        regle.setId(500);
        regle.setCode("REG-500");
        regle.setNom("Remise fideles");
        regle.setDescription("Remise de 10%");
        regle.setTypeRegle(RegleTarifaire.TypeRegle.REMISE_POURCENTAGE);
        regle.setValeur(new BigDecimal("10"));
        regle.setEstActif(Boolean.TRUE);
        regle.setDateDebut(sampleDebut());
        regle.setDateFin(sampleFin());
        regle.setQuantiteMinimale(new BigDecimal("5"));
        regle.setMontantMinimal(new BigDecimal("50.00"));
        regle.setEntreprise(sampleEntreprise());
        regle.setCategorie(sampleCategorie());
        regle.setClient(sampleClient());
        return regle;
    }

    @Nested
    @DisplayName("fromEntity")
    class FromEntity {

        @Test
        @DisplayName("null renvoie null (pas de NullPointerException)")
        void shouldReturnNullForNullEntity() {
            assertNull(RegleTarifaireDto.fromEntity(null));
        }

        @Test
        @DisplayName("copie tous les champs simples et derive les associations")
        void shouldMapAllFields() {
            RegleTarifaireDto dto = RegleTarifaireDto.fromEntity(sampleEntity());

            assertEquals(Integer.valueOf(500), dto.getId());
            assertEquals("REG-500", dto.getCode());
            assertEquals("Remise fideles", dto.getNom());
            assertEquals("Remise de 10%", dto.getDescription());
            assertEquals(RegleTarifaire.TypeRegle.REMISE_POURCENTAGE, dto.getTypeRegle());
            assertEquals(new BigDecimal("10"), dto.getValeur());
            assertEquals(Boolean.TRUE, dto.getEstActif());
            assertEquals(sampleDebut(), dto.getDateDebut());
            assertEquals(sampleFin(), dto.getDateFin());
            assertEquals(new BigDecimal("5"), dto.getQuantiteMinimale());
            assertEquals(new BigDecimal("50.00"), dto.getMontantMinimal());
            assertEquals(Integer.valueOf(9), dto.getEntrepriseId());
            assertEquals("Ma societe", dto.getEntrepriseNom());
            assertEquals(Integer.valueOf(2), dto.getCategorieId());
            assertEquals("Boissons", dto.getCategorieDesignation());
            assertEquals(Integer.valueOf(3), dto.getClientId());
            assertEquals("Dupont", dto.getClientNom());
        }

        @Test
        @DisplayName("tolerant aux associations nulles")
        void shouldTolerateNullAssociations() {
            RegleTarifaire regle = sampleEntity();
            regle.setEntreprise(null);
            regle.setCategorie(null);
            regle.setClient(null);

            RegleTarifaireDto dto = RegleTarifaireDto.fromEntity(regle);

            assertNull(dto.getEntrepriseId());
            assertNull(dto.getEntrepriseNom());
            assertNull(dto.getCategorieId());
            assertNull(dto.getCategorieDesignation());
            assertNull(dto.getClientId());
            assertNull(dto.getClientNom());
        }
    }

    @Nested
    @DisplayName("toEntity")
    class ToEntity {

        @Test
        @DisplayName("null renvoie null (pas de NullPointerException)")
        void shouldReturnNullForNullDto() {
            assertNull(RegleTarifaireDto.toEntity(null));
        }

        @Test
        @DisplayName("copie tous les champs simples")
        void shouldMapFields() {
            RegleTarifaire regle = RegleTarifaireDto.toEntity(sampleDto());

            assertEquals(Integer.valueOf(500), regle.getId());
            assertEquals("REG-500", regle.getCode());
            assertEquals("Remise fideles", regle.getNom());
            assertEquals("Remise de 10%", regle.getDescription());
            assertEquals(RegleTarifaire.TypeRegle.REMISE_POURCENTAGE, regle.getTypeRegle());
            assertEquals(new BigDecimal("10"), regle.getValeur());
            assertEquals(Boolean.TRUE, regle.getEstActif());
            assertEquals(sampleDebut(), regle.getDateDebut());
            assertEquals(sampleFin(), regle.getDateFin());
            assertEquals(new BigDecimal("5"), regle.getQuantiteMinimale());
            assertEquals(new BigDecimal("50.00"), regle.getMontantMinimal());
        }

        @Test
        @DisplayName("ne reconstruit pas les associations entreprise / categorie / client")
        void shouldNotRebuildAssociations() {
            RegleTarifaireDto dto = sampleDto();
            dto.setEntrepriseId(9);
            dto.setCategorieId(2);
            dto.setClientId(3);

            RegleTarifaire regle = RegleTarifaireDto.toEntity(dto);

            assertNull(regle.getEntreprise());
            assertNull(regle.getCategorie());
            assertNull(regle.getClient());
        }
    }

    @Test
    @DisplayName("aller-retour DTO -> entite -> DTO conserve les donnees")
    void roundTripShouldPreserveData() {
        RegleTarifaireDto original = sampleDto();
        RegleTarifaireDto result = RegleTarifaireDto.fromEntity(RegleTarifaireDto.toEntity(original));

        assertEquals(original, result);
    }
}
