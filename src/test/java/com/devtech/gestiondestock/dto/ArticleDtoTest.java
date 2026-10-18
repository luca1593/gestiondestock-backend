package com.devtech.gestiondestock.dto;

import com.devtech.gestiondestock.model.Article;
import com.devtech.gestiondestock.model.Category;
import com.devtech.gestiondestock.model.Entreprise;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Tests unitaires de la conversion DTO <-> entite pour {@link ArticleDto}.
 *
 * Particularite : fromEntity lit creationDate / lastModifiedDate (champs
 * d'audit herites d'AbstractEntity) mais toEntity ne les recopie pas.
 * Les listes (ligneVentes, mvtStks, ...) ne sont jamais mappees.
 */
class ArticleDtoTest {

    private Instant sampleCreationDate() {
        return Instant.parse("2026-01-01T10:00:00Z");
    }

    private Instant sampleLastModifiedDate() {
        return Instant.parse("2026-01-02T11:00:00Z");
    }

    private CategoryDto sampleCategoryDto() {
        return CategoryDto.builder()
                .id(2)
                .code("CAT-02")
                .designation("Boissons")
                .identreprise(9)
                .build();
    }

    private Category sampleCategory() {
        Category category = new Category();
        category.setId(2);
        category.setCode("CAT-02");
        category.setDesignation("Boissons");
        category.setIdentreprise(9);
        return category;
    }

    private EntrepriseDto sampleEntrepriseDto() {
        return EntrepriseDto.builder()
                .id(9)
                .nom("Ma societe")
                .email("contact@masociete.com")
                .build();
    }

    private Entreprise sampleEntreprise() {
        Entreprise entreprise = new Entreprise();
        entreprise.setId(9);
        entreprise.setNom("Ma societe");
        entreprise.setEmail("contact@masociete.com");
        return entreprise;
    }

    private ArticleDto sampleDto() {
        return ArticleDto.builder()
                .id(1)
                .codeArticle("ART-001")
                .designation("Cafe en grains")
                .prixUnitaireht(new BigDecimal("5.00"))
                .tauxTva(new BigDecimal("20.00"))
                .prixTtc(new BigDecimal("6.00"))
                .stock(new BigDecimal("100"))
                .photo("cafe.png")
                .category(sampleCategoryDto())
                .entreprise(sampleEntrepriseDto())
                .build();
    }

    private Article sampleEntity() {
        Article article = new Article();
        article.setId(1);
        article.setCodeArticle("ART-001");
        article.setDesignation("Cafe en grains");
        article.setPrixUnitaireht(new BigDecimal("5.00"));
        article.setTauxTva(new BigDecimal("20.00"));
        article.setPrixTtc(new BigDecimal("6.00"));
        article.setStock(new BigDecimal("100"));
        article.setPhoto("cafe.png");
        article.setCategory(sampleCategory());
        article.setEntreprise(sampleEntreprise());
        article.setCreationDate(sampleCreationDate());
        article.setLastModifiedDate(sampleLastModifiedDate());
        return article;
    }

    @Nested
    @DisplayName("fromEntity")
    class FromEntity {

        @Test
        @DisplayName("null renvoie null (pas de NullPointerException)")
        void shouldReturnNullForNullEntity() {
            assertNull(ArticleDto.fromEntity(null));
        }

        @Test
        @DisplayName("copie tous les champs simples, la categorie et l'entreprise")
        void shouldMapAllFields() {
            ArticleDto dto = ArticleDto.fromEntity(sampleEntity());

            assertEquals(Integer.valueOf(1), dto.getId());
            assertEquals("ART-001", dto.getCodeArticle());
            assertEquals("Cafe en grains", dto.getDesignation());
            assertEquals(new BigDecimal("5.00"), dto.getPrixUnitaireht());
            assertEquals(new BigDecimal("20.00"), dto.getTauxTva());
            assertEquals(new BigDecimal("6.00"), dto.getPrixTtc());
            assertEquals(new BigDecimal("100"), dto.getStock());
            assertEquals("cafe.png", dto.getPhoto());
            assertEquals(sampleCreationDate(), dto.getCreationDate());
            assertEquals(sampleLastModifiedDate(), dto.getLastModifiedDate());
            assertNotNull(dto.getCategory());
            assertEquals("Boissons", dto.getCategory().getDesignation());
            assertNotNull(dto.getEntreprise());
            assertEquals("Ma societe", dto.getEntreprise().getNom());
        }

        @Test
        @DisplayName("tolerant aux associations nulles")
        void shouldTolerateNullAssociations() {
            Article article = sampleEntity();
            article.setCategory(null);
            article.setEntreprise(null);

            ArticleDto dto = ArticleDto.fromEntity(article);

            assertNull(dto.getCategory());
            assertNull(dto.getEntreprise());
        }

        @Test
        @DisplayName("ne mappe aucune des listes associees")
        void shouldNotMapLists() {
            ArticleDto dto = ArticleDto.fromEntity(sampleEntity());

            assertNull(dto.getLigneVentes());
            assertNull(dto.getLigneCommandeClients());
            assertNull(dto.getLigneCommandeFournisseurs());
            assertNull(dto.getMvtStks());
        }
    }

    @Nested
    @DisplayName("toEntity")
    class ToEntity {

        @Test
        @DisplayName("null renvoie null (pas de NullPointerException)")
        void shouldReturnNullForNullDto() {
            assertNull(ArticleDto.toEntity(null));
        }

        @Test
        @DisplayName("copie tous les champs simples, la categorie et l'entreprise")
        void shouldMapAllFields() {
            Article article = ArticleDto.toEntity(sampleDto());

            assertEquals(Integer.valueOf(1), article.getId());
            assertEquals("ART-001", article.getCodeArticle());
            assertEquals("Cafe en grains", article.getDesignation());
            assertEquals(new BigDecimal("5.00"), article.getPrixUnitaireht());
            assertEquals(new BigDecimal("20.00"), article.getTauxTva());
            assertEquals(new BigDecimal("6.00"), article.getPrixTtc());
            assertEquals(new BigDecimal("100"), article.getStock());
            assertEquals("cafe.png", article.getPhoto());
            assertNotNull(article.getCategory());
            assertEquals("Boissons", article.getCategory().getDesignation());
            assertNotNull(article.getEntreprise());
            assertEquals("Ma societe", article.getEntreprise().getNom());
        }

        @Test
        @DisplayName("ne propage pas creationDate / lastModifiedDate (champs d'audit)")
        void shouldNotMapAuditFields() {
            ArticleDto dto = sampleDto();
            dto.setCreationDate(sampleCreationDate());
            dto.setLastModifiedDate(sampleLastModifiedDate());

            Article article = ArticleDto.toEntity(dto);

            assertNull(article.getCreationDate());
            assertNull(article.getLastModifiedDate());
        }
    }

    @Test
    @DisplayName("aller-retour DTO -> entite -> DTO conserve les donnees")
    void roundTripShouldPreserveData() {
        ArticleDto original = sampleDto();
        ArticleDto result = ArticleDto.fromEntity(ArticleDto.toEntity(original));

        assertEquals(original, result);
    }
}
