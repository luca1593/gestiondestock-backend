package com.devtech.gestiondestock.dto;

import com.devtech.gestiondestock.model.Article;
import com.devtech.gestiondestock.model.Inventaire;
import com.devtech.gestiondestock.model.InventaireLigne;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Tests unitaires de la conversion DTO <-> entite pour {@link InventaireLigneDto}.
 *
 * Particularite : fromEntity derive inventaireId / articleId / articleDesignation
 * depuis les associations, mais toEntity ne les reconstruit pas.
 */
class InventaireLigneDtoTest {

    private Inventaire sampleInventaire() {
        Inventaire inventaire = new Inventaire();
        inventaire.setId(200);
        return inventaire;
    }

    private Article sampleArticle() {
        Article article = new Article();
        article.setId(1);
        article.setCodeArticle("ART-001");
        article.setDesignation("Cafe en grains");
        return article;
    }

    private InventaireLigneDto sampleDto() {
        return InventaireLigneDto.builder()
                .id(210)
                .quantiteTheorique(new BigDecimal("100"))
                .quantiteReelle(new BigDecimal("98"))
                .build();
    }

    private InventaireLigne sampleEntity() {
        InventaireLigne ligne = new InventaireLigne();
        ligne.setId(210);
        ligne.setInventaire(sampleInventaire());
        ligne.setArticle(sampleArticle());
        ligne.setQuantiteTheorique(new BigDecimal("100"));
        ligne.setQuantiteReelle(new BigDecimal("98"));
        return ligne;
    }

    @Nested
    @DisplayName("fromEntity")
    class FromEntity {

        @Test
        @DisplayName("null renvoie null (pas de NullPointerException)")
        void shouldReturnNullForNullEntity() {
            assertNull(InventaireLigneDto.fromEntity(null));
        }

        @Test
        @DisplayName("copie les quantites et derive les identifiants / designation")
        void shouldMapAllFields() {
            InventaireLigneDto dto = InventaireLigneDto.fromEntity(sampleEntity());

            assertEquals(Integer.valueOf(210), dto.getId());
            assertEquals(Integer.valueOf(200), dto.getInventaireId());
            assertEquals(Integer.valueOf(1), dto.getArticleId());
            assertEquals("Cafe en grains", dto.getArticleDesignation());
            assertEquals(new BigDecimal("100"), dto.getQuantiteTheorique());
            assertEquals(new BigDecimal("98"), dto.getQuantiteReelle());
        }

        @Test
        @DisplayName("tolerant aux associations nulles")
        void shouldTolerateNullAssociations() {
            InventaireLigne ligne = sampleEntity();
            ligne.setInventaire(null);
            ligne.setArticle(null);

            InventaireLigneDto dto = InventaireLigneDto.fromEntity(ligne);

            assertNull(dto.getInventaireId());
            assertNull(dto.getArticleId());
            assertNull(dto.getArticleDesignation());
        }
    }

    @Nested
    @DisplayName("toEntity")
    class ToEntity {

        @Test
        @DisplayName("null renvoie null (pas de NullPointerException)")
        void shouldReturnNullForNullDto() {
            assertNull(InventaireLigneDto.toEntity(null));
        }

        @Test
        @DisplayName("copie id et quantites")
        void shouldMapFields() {
            InventaireLigne ligne = InventaireLigneDto.toEntity(sampleDto());

            assertEquals(Integer.valueOf(210), ligne.getId());
            assertEquals(new BigDecimal("100"), ligne.getQuantiteTheorique());
            assertEquals(new BigDecimal("98"), ligne.getQuantiteReelle());
        }

        @Test
        @DisplayName("ne reconstruit pas les associations inventaire / article")
        void shouldNotRebuildAssociations() {
            InventaireLigneDto dto = sampleDto();
            dto.setInventaireId(200);
            dto.setArticleId(1);
            dto.setArticleDesignation("Cafe en grains");

            InventaireLigne ligne = InventaireLigneDto.toEntity(dto);

            assertNull(ligne.getInventaire());
            assertNull(ligne.getArticle());
        }
    }

    @Test
    @DisplayName("aller-retour DTO -> entite -> DTO conserve les donnees")
    void roundTripShouldPreserveData() {
        InventaireLigneDto original = sampleDto();
        InventaireLigneDto result =
                InventaireLigneDto.fromEntity(InventaireLigneDto.toEntity(original));

        assertEquals(original, result);
    }
}
