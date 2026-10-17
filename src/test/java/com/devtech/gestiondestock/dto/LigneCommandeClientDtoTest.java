package com.devtech.gestiondestock.dto;

import com.devtech.gestiondestock.model.Article;
import com.devtech.gestiondestock.model.Client;
import com.devtech.gestiondestock.model.CommandeClient;
import com.devtech.gestiondestock.model.EtatCommande;
import com.devtech.gestiondestock.model.LigneCommandeClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Tests unitaires de la conversion DTO <-> entite pour {@link LigneCommandeClientDto}.
 *
 * Particularite : fromEntity recopie commandeClient, mais toEntity ne le
 * recopie pas. Le comportement reel est verrouille ici.
 */
class LigneCommandeClientDtoTest {

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

    private CommandeClientDto sampleCommandeDto() {
        return CommandeClientDto.builder()
                .id(50)
                .code("CC-050")
                .etatcommande(EtatCommande.EN_PREPARATION)
                .client(ClientDto.builder().id(3).nom("Dupont").build())
                .build();
    }

    private CommandeClient sampleCommande() {
        CommandeClient commande = new CommandeClient();
        commande.setId(50);
        commande.setCode("CC-050");
        commande.setEtatcommande(EtatCommande.EN_PREPARATION);
        Client client = new Client();
        client.setId(3);
        client.setNom("Dupont");
        commande.setClient(client);
        return commande;
    }

    private LigneCommandeClientDto sampleDto() {
        return LigneCommandeClientDto.builder()
                .id(90)
                .article(sampleArticleDto())
                .quantite(new BigDecimal("2"))
                .prixUnitaire(new BigDecimal("5.00"))
                .identreprise(9)
                .build();
    }

    private LigneCommandeClient sampleEntity() {
        LigneCommandeClient ligne = new LigneCommandeClient();
        ligne.setId(90);
        ligne.setArticle(sampleArticle());
        ligne.setCommandeClient(sampleCommande());
        ligne.setQuantite(new BigDecimal("2"));
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
            assertNull(LigneCommandeClientDto.fromEntity(null));
        }

        @Test
        @DisplayName("copie tous les champs et imbrique article / commandeClient")
        void shouldMapAllFields() {
            LigneCommandeClientDto dto = LigneCommandeClientDto.fromEntity(sampleEntity());

            assertEquals(Integer.valueOf(90), dto.getId());
            assertEquals(new BigDecimal("2"), dto.getQuantite());
            assertEquals(new BigDecimal("5.00"), dto.getPrixUnitaire());
            assertEquals(Integer.valueOf(9), dto.getIdentreprise());
            assertNotNull(dto.getArticle());
            assertEquals("ART-001", dto.getArticle().getCodeArticle());
            assertNotNull(dto.getCommandeClient());
            assertEquals("CC-050", dto.getCommandeClient().getCode());
        }
    }

    @Nested
    @DisplayName("toEntity")
    class ToEntity {

        @Test
        @DisplayName("null renvoie null (pas de NullPointerException)")
        void shouldReturnNullForNullDto() {
            assertNull(LigneCommandeClientDto.toEntity(null));
        }

        @Test
        @DisplayName("copie tous les champs et imbrique l'article")
        void shouldMapAllFields() {
            LigneCommandeClient ligne = LigneCommandeClientDto.toEntity(sampleDto());

            assertEquals(Integer.valueOf(90), ligne.getId());
            assertEquals(new BigDecimal("2"), ligne.getQuantite());
            assertEquals(new BigDecimal("5.00"), ligne.getPrixUnitaire());
            assertEquals(Integer.valueOf(9), ligne.getIdentreprise());
            assertNotNull(ligne.getArticle());
            assertEquals("ART-001", ligne.getArticle().getCodeArticle());
        }

        @Test
        @DisplayName("ne recopie pas commandeClient")
        void shouldNotMapCommandeClient() {
            LigneCommandeClientDto dto = sampleDto();
            dto.setCommandeClient(sampleCommandeDto());

            assertNull(LigneCommandeClientDto.toEntity(dto).getCommandeClient());
        }
    }

    @Test
    @DisplayName("aller-retour DTO -> entite -> DTO conserve les donnees")
    void roundTripShouldPreserveData() {
        LigneCommandeClientDto original = sampleDto();
        LigneCommandeClientDto result =
                LigneCommandeClientDto.fromEntity(LigneCommandeClientDto.toEntity(original));

        assertEquals(original, result);
    }
}
