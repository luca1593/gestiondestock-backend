package com.devtech.gestiondestock.dto;

import com.devtech.gestiondestock.model.Article;
import com.devtech.gestiondestock.model.Entreprise;
import com.devtech.gestiondestock.model.Entrepot;
import com.devtech.gestiondestock.model.Inventaire;
import com.devtech.gestiondestock.model.InventaireLigne;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Tests unitaires de la conversion DTO <-> entite pour {@link InventaireDto}.
 *
 * Particularites :
 * - fromEntity convertit la liste des lignes et renvoie une liste vide
 *   (jamais null) lorsque l'entite n'en a pas ;
 * - toEntity ne recopie ni les associations ni les lignes.
 */
class InventaireDtoTest {

    private LocalDateTime sampleDebut() {
        return LocalDateTime.of(2026, 10, 1, 8, 0);
    }

    private LocalDateTime sampleFin() {
        return LocalDateTime.of(2026, 10, 1, 18, 0);
    }

    private Entreprise sampleEntreprise() {
        Entreprise entreprise = new Entreprise();
        entreprise.setId(9);
        entreprise.setNom("Ma societe");
        return entreprise;
    }

    private Entrepot sampleEntrepot() {
        Entrepot entrepot = new Entrepot();
        entrepot.setId(5);
        entrepot.setNom("Entrepot principal");
        return entrepot;
    }

    private InventaireLigne sampleLigne() {
        Article article = new Article();
        article.setId(1);
        article.setDesignation("Cafe en grains");

        InventaireLigne ligne = new InventaireLigne();
        ligne.setId(210);
        ligne.setArticle(article);
        ligne.setQuantiteTheorique(new BigDecimal("100"));
        ligne.setQuantiteReelle(new BigDecimal("98"));
        return ligne;
    }

    private InventaireDto sampleDto() {
        return InventaireDto.builder()
                .id(200)
                .code("INV-200")
                .description("Inventaire annuel")
                .dateDebut(sampleDebut())
                .dateFin(sampleFin())
                .statut(Inventaire.Statut.EN_COURS)
                .lignes(Collections.emptyList())
                .build();
    }

    private Inventaire sampleEntity() {
        Inventaire inventaire = new Inventaire();
        inventaire.setId(200);
        inventaire.setCode("INV-200");
        inventaire.setDescription("Inventaire annuel");
        inventaire.setDateDebut(sampleDebut());
        inventaire.setDateFin(sampleFin());
        inventaire.setStatut(Inventaire.Statut.EN_COURS);
        inventaire.setEntreprise(sampleEntreprise());
        inventaire.setEntrepot(sampleEntrepot());
        inventaire.setLignes(List.of(sampleLigne()));
        return inventaire;
    }

    @Nested
    @DisplayName("fromEntity")
    class FromEntity {

        @Test
        @DisplayName("null renvoie null (pas de NullPointerException)")
        void shouldReturnNullForNullEntity() {
            assertNull(InventaireDto.fromEntity(null));
        }

        @Test
        @DisplayName("copie tous les champs, derive les associations et convertit les lignes")
        void shouldMapAllFields() {
            InventaireDto dto = InventaireDto.fromEntity(sampleEntity());

            assertEquals(Integer.valueOf(200), dto.getId());
            assertEquals("INV-200", dto.getCode());
            assertEquals("Inventaire annuel", dto.getDescription());
            assertEquals(sampleDebut(), dto.getDateDebut());
            assertEquals(sampleFin(), dto.getDateFin());
            assertEquals(Inventaire.Statut.EN_COURS, dto.getStatut());
            assertEquals(Integer.valueOf(9), dto.getEntrepriseId());
            assertEquals("Ma societe", dto.getEntrepriseNom());
            assertEquals(Integer.valueOf(5), dto.getEntrepotId());
            assertEquals("Entrepot principal", dto.getEntrepotNom());
            assertEquals(1, dto.getLignes().size());
            assertEquals("Cafe en grains", dto.getLignes().get(0).getArticleDesignation());
        }

        @Test
        @DisplayName("renvoie une liste vide lorsque l'entite n'a pas de lignes")
        void shouldReturnEmptyListWhenNoLignes() {
            Inventaire inventaire = sampleEntity();
            inventaire.setLignes(null);

            InventaireDto dto = InventaireDto.fromEntity(inventaire);

            assertNotNull(dto.getLignes());
            assertEquals(Collections.emptyList(), dto.getLignes());
        }

        @Test
        @DisplayName("tolerant aux associations nulles")
        void shouldTolerateNullAssociations() {
            Inventaire inventaire = sampleEntity();
            inventaire.setEntreprise(null);
            inventaire.setEntrepot(null);

            InventaireDto dto = InventaireDto.fromEntity(inventaire);

            assertNull(dto.getEntrepriseId());
            assertNull(dto.getEntrepriseNom());
            assertNull(dto.getEntrepotId());
            assertNull(dto.getEntrepotNom());
        }
    }

    @Nested
    @DisplayName("toEntity")
    class ToEntity {

        @Test
        @DisplayName("null renvoie null (pas de NullPointerException)")
        void shouldReturnNullForNullDto() {
            assertNull(InventaireDto.toEntity(null));
        }

        @Test
        @DisplayName("copie tous les champs simples")
        void shouldMapFields() {
            Inventaire inventaire = InventaireDto.toEntity(sampleDto());

            assertEquals(Integer.valueOf(200), inventaire.getId());
            assertEquals("INV-200", inventaire.getCode());
            assertEquals("Inventaire annuel", inventaire.getDescription());
            assertEquals(sampleDebut(), inventaire.getDateDebut());
            assertEquals(sampleFin(), inventaire.getDateFin());
            assertEquals(Inventaire.Statut.EN_COURS, inventaire.getStatut());
        }

        @Test
        @DisplayName("ne reconstruit ni les associations ni les lignes")
        void shouldNotRebuildAssociations() {
            InventaireDto dto = sampleDto();
            dto.setEntrepriseId(9);
            dto.setEntrepotId(5);
            dto.setLignes(List.of(InventaireLigneDto.builder().id(210).build()));

            Inventaire inventaire = InventaireDto.toEntity(dto);

            assertNull(inventaire.getEntreprise());
            assertNull(inventaire.getEntrepot());
            assertNull(inventaire.getLignes());
        }
    }

    @Test
    @DisplayName("aller-retour DTO -> entite -> DTO conserve les donnees")
    void roundTripShouldPreserveData() {
        InventaireDto original = sampleDto();
        InventaireDto result = InventaireDto.fromEntity(InventaireDto.toEntity(original));

        assertEquals(original, result);
    }
}
