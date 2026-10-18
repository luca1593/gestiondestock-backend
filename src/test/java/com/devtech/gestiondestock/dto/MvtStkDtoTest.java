package com.devtech.gestiondestock.dto;

import com.devtech.gestiondestock.model.Article;
import com.devtech.gestiondestock.model.MvtStk;
import com.devtech.gestiondestock.model.SourceMvtStk;
import com.devtech.gestiondestock.model.TypeMvt;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Tests unitaires de la conversion DTO <-> entite pour {@link MvtStkDto}.
 */
class MvtStkDtoTest {

    private Instant sampleDate() {
        return Instant.parse("2026-10-15T09:00:00Z");
    }

    private ArticleDto sampleArticleDto() {
        return ArticleDto.builder()
                .id(1)
                .codeArticle("ART-001")
                .designation("Cafe")
                .build();
    }

    private Article sampleArticle() {
        Article article = new Article();
        article.setId(1);
        article.setCodeArticle("ART-001");
        article.setDesignation("Cafe");
        return article;
    }

    private MvtStkDto sampleDto() {
        return MvtStkDto.builder()
                .id(21)
                .dateMvt(sampleDate())
                .quantite(new BigDecimal("25"))
                .article(sampleArticleDto())
                .typeMvt(TypeMvt.ENTRER)
                .sourceMvt(SourceMvtStk.COMMANDE_FOURNISSEUR)
                .identreprise(9)
                .build();
    }

    private MvtStk sampleEntity() {
        MvtStk mvtStk = new MvtStk();
        mvtStk.setId(21);
        mvtStk.setDateMvt(sampleDate());
        mvtStk.setQuantite(new BigDecimal("25"));
        mvtStk.setArticle(sampleArticle());
        mvtStk.setTypeMvt(TypeMvt.ENTRER);
        mvtStk.setSourceMvt(SourceMvtStk.COMMANDE_FOURNISSEUR);
        mvtStk.setIdentreprise(9);
        return mvtStk;
    }

    @Nested
    @DisplayName("fromEntity")
    class FromEntity {

        @Test
        @DisplayName("null renvoie null (pas de NullPointerException)")
        void shouldReturnNullForNullEntity() {
            assertNull(MvtStkDto.fromEntity(null));
        }

        @Test
        @DisplayName("copie tous les champs et imbrique l'article")
        void shouldMapAllFields() {
            MvtStkDto dto = MvtStkDto.fromEntity(sampleEntity());

            assertEquals(Integer.valueOf(21), dto.getId());
            assertEquals(sampleDate(), dto.getDateMvt());
            assertEquals(new BigDecimal("25"), dto.getQuantite());
            assertEquals(TypeMvt.ENTRER, dto.getTypeMvt());
            assertEquals(SourceMvtStk.COMMANDE_FOURNISSEUR, dto.getSourceMvt());
            assertEquals(Integer.valueOf(9), dto.getIdentreprise());
            assertNotNull(dto.getArticle());
            assertEquals("ART-001", dto.getArticle().getCodeArticle());
        }

        @Test
        @DisplayName("tolerant a l'article nul")
        void shouldTolerateNullArticle() {
            MvtStk mvtStk = sampleEntity();
            mvtStk.setArticle(null);

            assertNull(MvtStkDto.fromEntity(mvtStk).getArticle());
        }
    }

    @Nested
    @DisplayName("toEntity")
    class ToEntity {

        @Test
        @DisplayName("null renvoie null (pas de NullPointerException)")
        void shouldReturnNullForNullDto() {
            assertNull(MvtStkDto.toEntity(null));
        }

        @Test
        @DisplayName("copie tous les champs et imbrique l'article")
        void shouldMapAllFields() {
            MvtStk mvtStk = MvtStkDto.toEntity(sampleDto());

            assertEquals(Integer.valueOf(21), mvtStk.getId());
            assertEquals(sampleDate(), mvtStk.getDateMvt());
            assertEquals(new BigDecimal("25"), mvtStk.getQuantite());
            assertEquals(TypeMvt.ENTRER, mvtStk.getTypeMvt());
            assertEquals(SourceMvtStk.COMMANDE_FOURNISSEUR, mvtStk.getSourceMvt());
            assertEquals(Integer.valueOf(9), mvtStk.getIdentreprise());
            assertNotNull(mvtStk.getArticle());
            assertEquals("ART-001", mvtStk.getArticle().getCodeArticle());
        }
    }

    @Test
    @DisplayName("aller-retour DTO -> entite -> DTO conserve les donnees")
    void roundTripShouldPreserveData() {
        MvtStkDto original = sampleDto();
        MvtStkDto result = MvtStkDto.fromEntity(MvtStkDto.toEntity(original));

        assertEquals(original, result);
    }
}
