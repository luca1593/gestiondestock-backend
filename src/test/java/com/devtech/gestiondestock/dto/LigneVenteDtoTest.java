package com.devtech.gestiondestock.dto;

import com.devtech.gestiondestock.model.Article;
import com.devtech.gestiondestock.model.LigneVente;
import com.devtech.gestiondestock.model.Vente;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Tests unitaires de la conversion DTO <-> entite pour {@link LigneVenteDto}.
 */
class LigneVenteDtoTest {

    private Instant sampleDate() {
        return Instant.parse("2026-10-15T14:30:00Z");
    }

    private VenteDto sampleVenteDto() {
        return VenteDto.builder().id(11).code("VTE-011").dateVente(sampleDate()).build();
    }

    private Vente sampleVente() {
        Vente vente = new Vente();
        vente.setId(11);
        vente.setCode("VTE-011");
        vente.setDateVente(sampleDate());
        return vente;
    }

    private ArticleDto sampleArticleDto() {
        return ArticleDto.builder()
                .id(1)
                .codeArticle("ART-001")
                .designation("Cafe")
                .prixUnitaireht(new BigDecimal("5.00"))
                .build();
    }

    private Article sampleArticle() {
        Article article = new Article();
        article.setId(1);
        article.setCodeArticle("ART-001");
        article.setDesignation("Cafe");
        article.setPrixUnitaireht(new BigDecimal("5.00"));
        return article;
    }

    private LigneVenteDto sampleDto() {
        return LigneVenteDto.builder()
                .id(70)
                .vente(sampleVenteDto())
                .article(sampleArticleDto())
                .quantite(new BigDecimal("3"))
                .prixUnitaire(new BigDecimal("5.00"))
                .identreprise(9)
                .build();
    }

    private LigneVente sampleEntity() {
        LigneVente ligne = new LigneVente();
        ligne.setId(70);
        ligne.setVente(sampleVente());
        ligne.setArticle(sampleArticle());
        ligne.setQuantite(new BigDecimal("3"));
        ligne.setPrixUnitaire(new BigDecimal("5.00"));
        ligne.setIdentreprise(9);
        return ligne;
    }

    @Nested
    @DisplayName("fromEntity")
    class FromEntity {

        @Test
        @DisplayName("null renvoie null (pas de NullPointerException)")
        void shouldReturnNullForNullEntity() {
            assertNull(LigneVenteDto.fromEntity(null));
        }

        @Test
        @DisplayName("copie tous les champs et imbrique vente / article")
        void shouldMapAllFields() {
            LigneVenteDto dto = LigneVenteDto.fromEntity(sampleEntity());

            assertEquals(Integer.valueOf(70), dto.getId());
            assertEquals(new BigDecimal("3"), dto.getQuantite());
            assertEquals(new BigDecimal("5.00"), dto.getPrixUnitaire());
            assertEquals(Integer.valueOf(9), dto.getIdentreprise());
            assertNotNull(dto.getVente());
            assertEquals("VTE-011", dto.getVente().getCode());
            assertNotNull(dto.getArticle());
            assertEquals("ART-001", dto.getArticle().getCodeArticle());
        }

        @Test
        @DisplayName("tolerant a la vente et a l'article nuls")
        void shouldTolerateNullAssociations() {
            LigneVente ligne = sampleEntity();
            ligne.setVente(null);
            ligne.setArticle(null);

            LigneVenteDto dto = LigneVenteDto.fromEntity(ligne);

            assertNull(dto.getVente());
            assertNull(dto.getArticle());
        }
    }

    @Nested
    @DisplayName("toEntity")
    class ToEntity {

        @Test
        @DisplayName("null renvoie null (pas de NullPointerException)")
        void shouldReturnNullForNullDto() {
            assertNull(LigneVenteDto.toEntity(null));
        }

        @Test
        @DisplayName("copie tous les champs et imbrique vente / article")
        void shouldMapAllFields() {
            LigneVente ligne = LigneVenteDto.toEntity(sampleDto());

            assertEquals(Integer.valueOf(70), ligne.getId());
            assertEquals(new BigDecimal("3"), ligne.getQuantite());
            assertEquals(new BigDecimal("5.00"), ligne.getPrixUnitaire());
            assertEquals(Integer.valueOf(9), ligne.getIdentreprise());
            assertNotNull(ligne.getVente());
            assertEquals("VTE-011", ligne.getVente().getCode());
            assertNotNull(ligne.getArticle());
            assertEquals("ART-001", ligne.getArticle().getCodeArticle());
        }
    }

    @Test
    @DisplayName("aller-retour DTO -> entite -> DTO conserve les donnees")
    void roundTripShouldPreserveData() {
        LigneVenteDto original = sampleDto();
        LigneVenteDto result = LigneVenteDto.fromEntity(LigneVenteDto.toEntity(original));

        assertEquals(original, result);
    }
}
