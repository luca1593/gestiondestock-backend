package com.devtech.gestiondestock.dto;

import com.devtech.gestiondestock.model.Article;
import com.devtech.gestiondestock.model.CommandeFournisseur;
import com.devtech.gestiondestock.model.EtatCommande;
import com.devtech.gestiondestock.model.Fournisseur;
import com.devtech.gestiondestock.model.LigneCommandeFournisseur;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Tests unitaires de la conversion DTO <-> entite pour {@link LigneCommandeFournisseurDto}.
 *
 * Particularite : fromEntity recopie commandefournisseur, mais toEntity ne le
 * recopie pas. Le comportement reel est verrouille ici.
 */
class LigneCommandeFournisseurDtoTest {

    private ArticleDto sampleArticleDto() {
        return ArticleDto.builder().id(1).codeArticle("ART-001").designation("Cafe").build();
    }

    private Article sampleArticle() {
        Article article = new Article();
        article.setId(1);
        article.setCodeArticle("ART-001");
        article.setDesignation("Cafe");
        return article;
    }

    private CommandeFournisseurDto sampleCommandeDto() {
        return CommandeFournisseurDto.builder()
                .id(60)
                .code("CF-060")
                .etatcommande(EtatCommande.EN_PREPARATION)
                .fournisseur(FournisseurDto.builder().id(8).nom("Martin").build())
                .build();
    }

    private CommandeFournisseur sampleCommande() {
        CommandeFournisseur commande = new CommandeFournisseur();
        commande.setId(60);
        commande.setCode("CF-060");
        commande.setEtatcommande(EtatCommande.EN_PREPARATION);
        Fournisseur fournisseur = new Fournisseur();
        fournisseur.setId(8);
        fournisseur.setNom("Martin");
        commande.setFournisseur(fournisseur);
        return commande;
    }

    private LigneCommandeFournisseurDto sampleDto() {
        return LigneCommandeFournisseurDto.builder()
                .id(91)
                .article(sampleArticleDto())
                .quantite(new BigDecimal("4"))
                .prixUnitaire(new BigDecimal("3.50"))
                .identreprise(9)
                .build();
    }

    private LigneCommandeFournisseur sampleEntity() {
        LigneCommandeFournisseur ligne = new LigneCommandeFournisseur();
        ligne.setId(91);
        ligne.setArticle(sampleArticle());
        ligne.setCommandeFournisseur(sampleCommande());
        ligne.setQuantite(new BigDecimal("4"));
        ligne.setPrixUnitaire(new BigDecimal("3.50"));
        ligne.setIdentreprise(9);
        return ligne;
    }

    @Nested
    @DisplayName("fromEntity")
    class FromEntity {

        @Test
        @DisplayName("null renvoie null (pas de NullPointerException)")
        void shouldReturnNullForNullEntity() {
            assertNull(LigneCommandeFournisseurDto.fromEntity(null));
        }

        @Test
        @DisplayName("copie tous les champs et imbrique article / commandefournisseur")
        void shouldMapAllFields() {
            LigneCommandeFournisseurDto dto = LigneCommandeFournisseurDto.fromEntity(sampleEntity());

            assertEquals(Integer.valueOf(91), dto.getId());
            assertEquals(new BigDecimal("4"), dto.getQuantite());
            assertEquals(new BigDecimal("3.50"), dto.getPrixUnitaire());
            assertEquals(Integer.valueOf(9), dto.getIdentreprise());
            assertNotNull(dto.getArticle());
            assertEquals("ART-001", dto.getArticle().getCodeArticle());
            assertNotNull(dto.getCommandefournisseur());
            assertEquals("CF-060", dto.getCommandefournisseur().getCode());
        }
    }

    @Nested
    @DisplayName("toEntity")
    class ToEntity {

        @Test
        @DisplayName("null renvoie null (pas de NullPointerException)")
        void shouldReturnNullForNullDto() {
            assertNull(LigneCommandeFournisseurDto.toEntity(null));
        }

        @Test
        @DisplayName("copie tous les champs et imbrique l'article")
        void shouldMapAllFields() {
            LigneCommandeFournisseur ligne = LigneCommandeFournisseurDto.toEntity(sampleDto());

            assertEquals(Integer.valueOf(91), ligne.getId());
            assertEquals(new BigDecimal("4"), ligne.getQuantite());
            assertEquals(new BigDecimal("3.50"), ligne.getPrixUnitaire());
            assertEquals(Integer.valueOf(9), ligne.getIdentreprise());
            assertNotNull(ligne.getArticle());
            assertEquals("ART-001", ligne.getArticle().getCodeArticle());
        }

        @Test
        @DisplayName("ne recopie pas commandefournisseur")
        void shouldNotMapCommandeFournisseur() {
            LigneCommandeFournisseurDto dto = sampleDto();
            dto.setCommandefournisseur(sampleCommandeDto());

            assertNull(LigneCommandeFournisseurDto.toEntity(dto).getCommandeFournisseur());
        }
    }

    @Test
    @DisplayName("aller-retour DTO -> entite -> DTO conserve les donnees")
    void roundTripShouldPreserveData() {
        LigneCommandeFournisseurDto original = sampleDto();
        LigneCommandeFournisseurDto result =
                LigneCommandeFournisseurDto.fromEntity(LigneCommandeFournisseurDto.toEntity(original));

        assertEquals(original, result);
    }
}
