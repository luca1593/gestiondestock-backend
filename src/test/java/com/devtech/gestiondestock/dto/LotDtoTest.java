package com.devtech.gestiondestock.dto;

import com.devtech.gestiondestock.model.Article;
import com.devtech.gestiondestock.model.Entreprise;
import com.devtech.gestiondestock.model.Entrepot;
import com.devtech.gestiondestock.model.Lot;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Tests unitaires de la conversion DTO <-> entite pour {@link LotDto}.
 *
 * Particularite : fromEntity derive articleId / articleDesignation /
 * entrepotId / entrepotNom / entrepriseId / entrepriseNom, mais toEntity
 * ne reconstruit aucune de ces associations.
 */
class LotDtoTest {

    private LocalDateTime sampleFabrication() {
        return LocalDateTime.of(2026, 1, 10, 8, 0);
    }

    private LocalDateTime sampleExpiration() {
        return LocalDateTime.of(2027, 1, 10, 8, 0);
    }

    private Article sampleArticle() {
        Article article = new Article();
        article.setId(1);
        article.setDesignation("Cafe en grains");
        return article;
    }

    private Entrepot sampleEntrepot() {
        Entrepot entrepot = new Entrepot();
        entrepot.setId(5);
        entrepot.setNom("Entrepot principal");
        return entrepot;
    }

    private Entreprise sampleEntreprise() {
        Entreprise entreprise = new Entreprise();
        entreprise.setId(9);
        entreprise.setNom("Ma societe");
        return entreprise;
    }

    private LotDto sampleDto() {
        return LotDto.builder()
                .id(300)
                .numeroLot("LOT-300")
                .quantite(new BigDecimal("50"))
                .quantiteRestante(new BigDecimal("30"))
                .dateFabrication(sampleFabrication())
                .dateExpiration(sampleExpiration())
                .prixAchat(new BigDecimal("4.20"))
                .estCompletementUtilise(Boolean.TRUE)
                .build();
    }

    private Lot sampleEntity() {
        Lot lot = new Lot();
        lot.setId(300);
        lot.setNumeroLot("LOT-300");
        lot.setQuantite(new BigDecimal("50"));
        lot.setQuantiteRestante(new BigDecimal("30"));
        lot.setDateFabrication(sampleFabrication());
        lot.setDateExpiration(sampleExpiration());
        lot.setPrixAchat(new BigDecimal("4.20"));
        lot.setArticle(sampleArticle());
        lot.setEntrepot(sampleEntrepot());
        lot.setEntreprise(sampleEntreprise());
        lot.setEstCompletementUtilise(Boolean.TRUE);
        return lot;
    }

    @Nested
    @DisplayName("fromEntity")
    class FromEntity {

        @Test
        @DisplayName("null renvoie null (pas de NullPointerException)")
        void shouldReturnNullForNullEntity() {
            assertNull(LotDto.fromEntity(null));
        }

        @Test
        @DisplayName("copie tous les champs simples et derive les associations")
        void shouldMapAllFields() {
            LotDto dto = LotDto.fromEntity(sampleEntity());

            assertEquals(Integer.valueOf(300), dto.getId());
            assertEquals("LOT-300", dto.getNumeroLot());
            assertEquals(new BigDecimal("50"), dto.getQuantite());
            assertEquals(new BigDecimal("30"), dto.getQuantiteRestante());
            assertEquals(sampleFabrication(), dto.getDateFabrication());
            assertEquals(sampleExpiration(), dto.getDateExpiration());
            assertEquals(new BigDecimal("4.20"), dto.getPrixAchat());
            assertEquals(Boolean.TRUE, dto.getEstCompletementUtilise());
            assertEquals(Integer.valueOf(1), dto.getArticleId());
            assertEquals("Cafe en grains", dto.getArticleDesignation());
            assertEquals(Integer.valueOf(5), dto.getEntrepotId());
            assertEquals("Entrepot principal", dto.getEntrepotNom());
            assertEquals(Integer.valueOf(9), dto.getEntrepriseId());
            assertEquals("Ma societe", dto.getEntrepriseNom());
        }

        @Test
        @DisplayName("tolerant aux associations nulles")
        void shouldTolerateNullAssociations() {
            Lot lot = sampleEntity();
            lot.setArticle(null);
            lot.setEntrepot(null);
            lot.setEntreprise(null);

            LotDto dto = LotDto.fromEntity(lot);

            assertNull(dto.getArticleId());
            assertNull(dto.getArticleDesignation());
            assertNull(dto.getEntrepotId());
            assertNull(dto.getEntrepotNom());
            assertNull(dto.getEntrepriseId());
            assertNull(dto.getEntrepriseNom());
        }
    }

    @Nested
    @DisplayName("toEntity")
    class ToEntity {

        @Test
        @DisplayName("null renvoie null (pas de NullPointerException)")
        void shouldReturnNullForNullDto() {
            assertNull(LotDto.toEntity(null));
        }

        @Test
        @DisplayName("copie tous les champs simples")
        void shouldMapFields() {
            Lot lot = LotDto.toEntity(sampleDto());

            assertEquals(Integer.valueOf(300), lot.getId());
            assertEquals("LOT-300", lot.getNumeroLot());
            assertEquals(new BigDecimal("50"), lot.getQuantite());
            assertEquals(new BigDecimal("30"), lot.getQuantiteRestante());
            assertEquals(sampleFabrication(), lot.getDateFabrication());
            assertEquals(sampleExpiration(), lot.getDateExpiration());
            assertEquals(new BigDecimal("4.20"), lot.getPrixAchat());
            assertEquals(Boolean.TRUE, lot.getEstCompletementUtilise());
        }

        @Test
        @DisplayName("ne reconstruit pas les associations article / entrepot / entreprise")
        void shouldNotRebuildAssociations() {
            LotDto dto = sampleDto();
            dto.setArticleId(1);
            dto.setEntrepotId(5);
            dto.setEntrepriseId(9);

            Lot lot = LotDto.toEntity(dto);

            assertNull(lot.getArticle());
            assertNull(lot.getEntrepot());
            assertNull(lot.getEntreprise());
        }
    }

    @Test
    @DisplayName("aller-retour DTO -> entite -> DTO conserve les donnees")
    void roundTripShouldPreserveData() {
        LotDto original = sampleDto();
        LotDto result = LotDto.fromEntity(LotDto.toEntity(original));

        assertEquals(original, result);
    }
}
