package com.devtech.gestiondestock.validator;

import com.devtech.gestiondestock.dto.ArticleDto;
import com.devtech.gestiondestock.dto.CategoryDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests unitaires du validateur d'article.
 *
 * On verifie le comportement attendu cote utilisateur : un article complet
 * passe, un article incomplet est refuse avec un message explicite par champ.
 */
class ArticleValidatorTest {

    private ArticleDto.ArticleDtoBuilder validArticle() {
        return ArticleDto.builder()
                .codeArticle("ART-001")
                .designation("Article de test")
                .prixUnitaireht(new BigDecimal("100.00"))
                .tauxTva(new BigDecimal("18.00"))
                .prixTtc(new BigDecimal("118.00"))
                .category(CategoryDto.builder().id(1).code("CAT-001").build());
    }

    @Test
    @DisplayName("Article null : une erreur est remontee pour chaque champ obligatoire")
    void shouldRejectNullArticle() {
        List<String> errors = ArticleValidator.validate(null);

        assertEquals(6, errors.size(), "Un article null doit declencher les 6 messages obligatoires");
        assertTrue(errors.contains("Veuillez renseigner le code de l'article"));
        assertTrue(errors.contains("Veuillez selectionner une categorie pour l'article"));
    }

    @Test
    @DisplayName("Article complet : aucune erreur")
    void shouldAcceptValidArticle() {
        assertTrue(ArticleValidator.validate(validArticle().build()).isEmpty());
    }

    @Test
    @DisplayName("Code article vide : une seule erreur, sur le code")
    void shouldRejectBlankCode() {
        List<String> errors = ArticleValidator.validate(validArticle().codeArticle("").build());

        assertEquals(1, errors.size());
        assertEquals("Veuillez renseigner le code de l'article", errors.get(0));
    }

    @Test
    @DisplayName("Categorie absente : erreur sur la categorie uniquement")
    void shouldRejectMissingCategory() {
        List<String> errors = ArticleValidator.validate(validArticle().category(null).build());

        assertEquals(1, errors.size());
        assertEquals("Veuillez selectionner une categorie pour l'article", errors.get(0));
    }

    @Test
    @DisplayName("Prix null : erreur sur chaque prix manquant")
    void shouldRejectNullPrices() {
        List<String> errors = ArticleValidator.validate(
                validArticle().prixUnitaireht(null).tauxTva(null).prixTtc(null).build());

        assertEquals(3, errors.size());
        assertTrue(errors.contains("Veuillez renseigner le prix unitaire de l'article"));
        assertTrue(errors.contains("Veuillez renseigner le code TVA de l'article"));
        assertTrue(errors.contains("Veuillez renseigner le prix unitaire TTC de l'article"));
    }
}
