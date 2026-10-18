package com.devtech.gestiondestock.dto;

import com.devtech.gestiondestock.model.Client;
import com.devtech.gestiondestock.model.Entreprise;
import com.devtech.gestiondestock.model.Facture;
import com.devtech.gestiondestock.model.Fournisseur;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Tests unitaires de la conversion DTO <-> entite pour {@link FactureDto}.
 *
 * Particularites :
 * - fromEntity derive client / fournisseur / entreprise (id + nom) ;
 * - toEntity ne reconstruit pas ces associations ;
 * - le champ paiement n'est mappe dans aucun sens (saisie uniquement).
 */
class FactureDtoTest {

    private LocalDateTime sampleDateFacture() {
        return LocalDateTime.of(2026, 10, 16, 10, 0);
    }

    private LocalDateTime sampleDateEcheance() {
        return LocalDateTime.of(2026, 11, 16, 10, 0);
    }

    private Client sampleClient() {
        Client client = new Client();
        client.setId(3);
        client.setNom("Dupont");
        return client;
    }

    private Fournisseur sampleFournisseur() {
        Fournisseur fournisseur = new Fournisseur();
        fournisseur.setId(8);
        fournisseur.setNom("Martin");
        return fournisseur;
    }

    private Entreprise sampleEntreprise() {
        Entreprise entreprise = new Entreprise();
        entreprise.setId(9);
        entreprise.setNom("Ma societe");
        return entreprise;
    }

    private FactureDto sampleDto() {
        return FactureDto.builder()
                .id(700)
                .numeroFacture("FAC-2026-700")
                .dateFacture(sampleDateFacture())
                .dateEcheance(sampleDateEcheance())
                .statut(Facture.StatutFacture.EN_ATTENTE)
                .typeFacture(Facture.TypeFacture.CLIENT)
                .sourceType(Facture.SourceDocument.VENTE)
                .sourceId(11)
                .montantHT(new BigDecimal("100.00"))
                .montantTVA(new BigDecimal("20.00"))
                .montantTTC(new BigDecimal("120.00"))
                .montantPaye(new BigDecimal("40.00"))
                .montantRestant(new BigDecimal("80.00"))
                .build();
    }

    private Facture sampleEntity() {
        Facture facture = new Facture();
        facture.setId(700);
        facture.setNumeroFacture("FAC-2026-700");
        facture.setDateFacture(sampleDateFacture());
        facture.setDateEcheance(sampleDateEcheance());
        facture.setStatut(Facture.StatutFacture.EN_ATTENTE);
        facture.setTypeFacture(Facture.TypeFacture.CLIENT);
        facture.setSourceType(Facture.SourceDocument.VENTE);
        facture.setSourceId(11);
        facture.setMontantHT(new BigDecimal("100.00"));
        facture.setMontantTVA(new BigDecimal("20.00"));
        facture.setMontantTTC(new BigDecimal("120.00"));
        facture.setMontantPaye(new BigDecimal("40.00"));
        facture.setMontantRestant(new BigDecimal("80.00"));
        facture.setClient(sampleClient());
        facture.setFournisseur(sampleFournisseur());
        facture.setEntreprise(sampleEntreprise());
        return facture;
    }

    @Nested
    @DisplayName("fromEntity")
    class FromEntity {

        @Test
        @DisplayName("null renvoie null (pas de NullPointerException)")
        void shouldReturnNullForNullEntity() {
            assertNull(FactureDto.fromEntity(null));
        }

        @Test
        @DisplayName("copie tous les champs simples et derive les associations")
        void shouldMapAllFields() {
            FactureDto dto = FactureDto.fromEntity(sampleEntity());

            assertEquals(Integer.valueOf(700), dto.getId());
            assertEquals("FAC-2026-700", dto.getNumeroFacture());
            assertEquals(sampleDateFacture(), dto.getDateFacture());
            assertEquals(sampleDateEcheance(), dto.getDateEcheance());
            assertEquals(Facture.StatutFacture.EN_ATTENTE, dto.getStatut());
            assertEquals(Facture.TypeFacture.CLIENT, dto.getTypeFacture());
            assertEquals(Facture.SourceDocument.VENTE, dto.getSourceType());
            assertEquals(Integer.valueOf(11), dto.getSourceId());
            assertEquals(new BigDecimal("100.00"), dto.getMontantHT());
            assertEquals(new BigDecimal("20.00"), dto.getMontantTVA());
            assertEquals(new BigDecimal("120.00"), dto.getMontantTTC());
            assertEquals(new BigDecimal("40.00"), dto.getMontantPaye());
            assertEquals(new BigDecimal("80.00"), dto.getMontantRestant());
            assertEquals(Integer.valueOf(3), dto.getClientId());
            assertEquals("Dupont", dto.getClientNom());
            assertEquals(Integer.valueOf(8), dto.getFournisseurId());
            assertEquals("Martin", dto.getFournisseurNom());
            assertEquals(Integer.valueOf(9), dto.getEntrepriseId());
            assertEquals("Ma societe", dto.getEntrepriseNom());
        }

        @Test
        @DisplayName("tolerant aux associations nulles")
        void shouldTolerateNullAssociations() {
            Facture facture = sampleEntity();
            facture.setClient(null);
            facture.setFournisseur(null);
            facture.setEntreprise(null);

            FactureDto dto = FactureDto.fromEntity(facture);

            assertNull(dto.getClientId());
            assertNull(dto.getClientNom());
            assertNull(dto.getFournisseurId());
            assertNull(dto.getFournisseurNom());
            assertNull(dto.getEntrepriseId());
            assertNull(dto.getEntrepriseNom());
        }

        @Test
        @DisplayName("ne renseigne pas le champ paiement")
        void shouldNotMapPaiement() {
            assertNull(FactureDto.fromEntity(sampleEntity()).getPaiement());
        }
    }

    @Nested
    @DisplayName("toEntity")
    class ToEntity {

        @Test
        @DisplayName("null renvoie null (pas de NullPointerException)")
        void shouldReturnNullForNullDto() {
            assertNull(FactureDto.toEntity(null));
        }

        @Test
        @DisplayName("copie tous les champs simples")
        void shouldMapFields() {
            Facture facture = FactureDto.toEntity(sampleDto());

            assertEquals(Integer.valueOf(700), facture.getId());
            assertEquals("FAC-2026-700", facture.getNumeroFacture());
            assertEquals(sampleDateFacture(), facture.getDateFacture());
            assertEquals(sampleDateEcheance(), facture.getDateEcheance());
            assertEquals(Facture.StatutFacture.EN_ATTENTE, facture.getStatut());
            assertEquals(Facture.TypeFacture.CLIENT, facture.getTypeFacture());
            assertEquals(Facture.SourceDocument.VENTE, facture.getSourceType());
            assertEquals(Integer.valueOf(11), facture.getSourceId());
            assertEquals(new BigDecimal("100.00"), facture.getMontantHT());
            assertEquals(new BigDecimal("20.00"), facture.getMontantTVA());
            assertEquals(new BigDecimal("120.00"), facture.getMontantTTC());
            assertEquals(new BigDecimal("40.00"), facture.getMontantPaye());
            assertEquals(new BigDecimal("80.00"), facture.getMontantRestant());
        }

        @Test
        @DisplayName("ne reconstruit pas les associations client / fournisseur / entreprise")
        void shouldNotRebuildAssociations() {
            FactureDto dto = sampleDto();
            dto.setClientId(3);
            dto.setFournisseurId(8);
            dto.setEntrepriseId(9);

            Facture facture = FactureDto.toEntity(dto);

            assertNull(facture.getClient());
            assertNull(facture.getFournisseur());
            assertNull(facture.getEntreprise());
        }
    }

    @Test
    @DisplayName("aller-retour DTO -> entite -> DTO conserve les donnees")
    void roundTripShouldPreserveData() {
        FactureDto original = sampleDto();
        FactureDto result = FactureDto.fromEntity(FactureDto.toEntity(original));

        assertEquals(original, result);
    }
}
