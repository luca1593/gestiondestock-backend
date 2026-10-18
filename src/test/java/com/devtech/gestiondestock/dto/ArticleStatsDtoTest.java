package com.devtech.gestiondestock.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Tests Lombok de {@link ArticleStatsDto} (aucune conversion entite).
 *
 * On verifie la construction via builder, les accesseurs, l'egalite
 * fournie par @Data et la presence des constructeurs par defaut / tous args.
 */
class ArticleStatsDtoTest {

    private ArticleStatsDto sample() {
        return ArticleStatsDto.builder()
                .articleId(42)
                .codeArticle("ART-042")
                .designation("Cafe en grains")
                .stock(new BigDecimal("12.50"))
                .prixUnitaire(new BigDecimal("7.90"))
                .category("Boissons")
                .nbVentes(15L)
                .nbCommandesClient(3L)
                .nbCommandesFournisseur(2L)
                .build();
    }

    @Test
    @DisplayName("le builder renseigne tous les champs")
    void builderShouldFillAllFields() {
        ArticleStatsDto dto = sample();

        assertEquals(Integer.valueOf(42), dto.getArticleId());
        assertEquals("ART-042", dto.getCodeArticle());
        assertEquals("Cafe en grains", dto.getDesignation());
        assertEquals(new BigDecimal("12.50"), dto.getStock());
        assertEquals(new BigDecimal("7.90"), dto.getPrixUnitaire());
        assertEquals("Boissons", dto.getCategory());
        assertEquals(Long.valueOf(15L), dto.getNbVentes());
        assertEquals(Long.valueOf(3L), dto.getNbCommandesClient());
        assertEquals(Long.valueOf(2L), dto.getNbCommandesFournisseur());
    }

    @Test
    @DisplayName("les setters modifient les champs")
    void settersShouldUpdateFields() {
        ArticleStatsDto dto = new ArticleStatsDto();
        dto.setArticleId(1);
        dto.setCodeArticle("ART-001");
        dto.setDesignation("The");
        dto.setStock(new BigDecimal("1"));
        dto.setPrixUnitaire(new BigDecimal("2"));
        dto.setCategory("Boissons");
        dto.setNbVentes(4L);
        dto.setNbCommandesClient(5L);
        dto.setNbCommandesFournisseur(6L);

        assertEquals(Integer.valueOf(1), dto.getArticleId());
        assertEquals("ART-001", dto.getCodeArticle());
        assertEquals("The", dto.getDesignation());
        assertEquals(new BigDecimal("1"), dto.getStock());
        assertEquals(new BigDecimal("2"), dto.getPrixUnitaire());
        assertEquals("Boissons", dto.getCategory());
        assertEquals(Long.valueOf(4L), dto.getNbVentes());
        assertEquals(Long.valueOf(5L), dto.getNbCommandesClient());
        assertEquals(Long.valueOf(6L), dto.getNbCommandesFournisseur());
    }

    @Test
    @DisplayName("@Data fournit equals et hashCode")
    void equalsAndHashCodeShouldBeValueBased() {
        ArticleStatsDto a = sample();
        ArticleStatsDto b = sample();
        ArticleStatsDto c = ArticleStatsDto.builder().articleId(99).build();

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a, c);
    }

    @Test
    @DisplayName("le constructeur par defaut laisse les champs a null")
    void defaultConstructorShouldLeaveFieldsNull() {
        ArticleStatsDto dto = new ArticleStatsDto();

        assertNull(dto.getArticleId());
        assertNull(dto.getDesignation());
        assertNull(dto.getNbVentes());
    }

    @Test
    @DisplayName("le constructeur avec tous les arguments renseigne les champs")
    void allArgsConstructorShouldFillFields() {
        ArticleStatsDto dto = new ArticleStatsDto(
                42, "ART-042", "Cafe",
                new BigDecimal("12.50"), new BigDecimal("7.90"), "Boissons",
                15L, 3L, 2L);

        assertEquals(Integer.valueOf(42), dto.getArticleId());
        assertEquals("ART-042", dto.getCodeArticle());
        assertEquals("Boissons", dto.getCategory());
        assertEquals(Long.valueOf(2L), dto.getNbCommandesFournisseur());
    }
}
