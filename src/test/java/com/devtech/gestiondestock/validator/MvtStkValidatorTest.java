package com.devtech.gestiondestock.validator;

import com.devtech.gestiondestock.dto.ArticleDto;
import com.devtech.gestiondestock.dto.MvtStkDto;
import com.devtech.gestiondestock.model.TypeMvt;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests unitaires du validateur de mouvement de stock.
 *
 * Champs obligatoires : dateMvt, quantite (non nulle), article (avec id), typeMvt.
 */
class MvtStkValidatorTest {

    private MvtStkDto.MvtStkDtoBuilder validMvtStk() {
        return MvtStkDto.builder()
                .dateMvt(Instant.parse("2026-01-01T10:00:00Z"))
                .quantite(new BigDecimal("10"))
                .article(ArticleDto.builder().id(1).build())
                .typeMvt(TypeMvt.ENTRER);
    }

    @Test
    @DisplayName("Mouvement null : les 4 messages obligatoires")
    void shouldRejectNullMvtStk() {
        List<String> errors = MvtStkValidator.validate(null);

        assertFalse(errors.isEmpty());
        assertEquals(4, errors.size());
        assertTrue(errors.contains("Veuillez renseigner la date du mouvement"));
        assertTrue(errors.contains("Veuillez renseigner la quantite du mouvement"));
        assertTrue(errors.contains("Veuillez renseigner la type du mouvement"));
        assertTrue(errors.contains("Veuillez renseigner l'article du mouvement"));
    }

    @Test
    @DisplayName("Mouvement complet : aucune erreur")
    void shouldAcceptValidMvtStk() {
        assertTrue(MvtStkValidator.validate(validMvtStk().build()).isEmpty());
    }

    @Test
    @DisplayName("Date absente : une seule erreur")
    void shouldRejectMissingDate() {
        List<String> errors = MvtStkValidator.validate(validMvtStk().dateMvt(null).build());

        assertEquals(1, errors.size());
        assertEquals("Veuillez renseigner la date du mouvement", errors.get(0));
    }

    @Test
    @DisplayName("Quantite absente : une seule erreur")
    void shouldRejectMissingQuantite() {
        List<String> errors = MvtStkValidator.validate(validMvtStk().quantite(null).build());

        assertEquals(1, errors.size());
        assertEquals("Veuillez renseigner la quantite du mouvement", errors.get(0));
    }

    @Test
    @DisplayName("Quantite nulle : une seule erreur")
    void shouldRejectZeroQuantite() {
        List<String> errors = MvtStkValidator.validate(validMvtStk().quantite(BigDecimal.ZERO).build());

        assertEquals(1, errors.size());
        assertEquals("Veuillez renseigner la quantite du mouvement", errors.get(0));
    }

    @Test
    @DisplayName("Quantite negative : acceptee (seuls null et zero sont refuses)")
    void shouldAcceptNegativeQuantite() {
        // Comportement actuel verrouille : compareTo(ZERO) == 0 ne detecte pas les negatifs.
        assertTrue(MvtStkValidator.validate(validMvtStk().quantite(new BigDecimal("-5")).build()).isEmpty());
    }

    @Test
    @DisplayName("Article absent : une seule erreur")
    void shouldRejectMissingArticle() {
        List<String> errors = MvtStkValidator.validate(validMvtStk().article(null).build());

        assertEquals(1, errors.size());
        assertEquals("Veuillez renseigner l'article du mouvement", errors.get(0));
    }

    @Test
    @DisplayName("Article sans id : une seule erreur")
    void shouldRejectArticleWithoutId() {
        List<String> errors = MvtStkValidator.validate(
                validMvtStk().article(ArticleDto.builder().codeArticle("ART-001").build()).build());

        assertEquals(1, errors.size());
        assertEquals("Veuillez renseigner l'article du mouvement", errors.get(0));
    }

    @Test
    @DisplayName("Type de mouvement absent : une seule erreur")
    void shouldRejectMissingTypeMvt() {
        List<String> errors = MvtStkValidator.validate(validMvtStk().typeMvt(null).build());

        assertEquals(1, errors.size());
        assertEquals("Veuillez renseigner la type du mouvement", errors.get(0));
    }
}
