package com.devtech.gestiondestock.dto;

import com.devtech.gestiondestock.model.Entreprise;
import com.devtech.gestiondestock.model.Facture;
import com.devtech.gestiondestock.model.Paiement;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Tests unitaires de la conversion DTO <-> entite pour {@link PaiementDto}.
 *
 * Particularite : fromEntity derive factureId / factureNumero / entrepriseId /
 * entrepriseNom, mais toEntity ne reconstruit pas ces associations.
 */
class PaiementDtoTest {

    private LocalDateTime sampleDate() {
        return LocalDateTime.of(2026, 10, 16, 12, 30);
    }

    private Facture sampleFacture() {
        Facture facture = new Facture();
        facture.setId(400);
        facture.setNumeroFacture("FAC-2026-400");
        return facture;
    }

    private Entreprise sampleEntreprise() {
        Entreprise entreprise = new Entreprise();
        entreprise.setId(9);
        entreprise.setNom("Ma societe");
        return entreprise;
    }

    private PaiementDto sampleDto() {
        return PaiementDto.builder()
                .id(410)
                .montant(new BigDecimal("100.00"))
                .montantDonne(new BigDecimal("120.00"))
                .monnaie(new BigDecimal("20.00"))
                .datePaiement(sampleDate())
                .modePaiement(Paiement.ModePaiement.ESPECES)
                .reference("REF-410")
                .note("Paiement comptoir")
                .build();
    }

    private Paiement sampleEntity() {
        Paiement paiement = new Paiement();
        paiement.setId(410);
        paiement.setMontant(new BigDecimal("100.00"));
        paiement.setMontantDonne(new BigDecimal("120.00"));
        paiement.setMonnaie(new BigDecimal("20.00"));
        paiement.setDatePaiement(sampleDate());
        paiement.setModePaiement(Paiement.ModePaiement.ESPECES);
        paiement.setReference("REF-410");
        paiement.setNote("Paiement comptoir");
        paiement.setFacture(sampleFacture());
        paiement.setEntreprise(sampleEntreprise());
        return paiement;
    }

    @Nested
    @DisplayName("fromEntity")
    class FromEntity {

        @Test
        @DisplayName("null renvoie null (pas de NullPointerException)")
        void shouldReturnNullForNullEntity() {
            assertNull(PaiementDto.fromEntity(null));
        }

        @Test
        @DisplayName("copie tous les champs simples et derive les associations")
        void shouldMapAllFields() {
            PaiementDto dto = PaiementDto.fromEntity(sampleEntity());

            assertEquals(Integer.valueOf(410), dto.getId());
            assertEquals(new BigDecimal("100.00"), dto.getMontant());
            assertEquals(new BigDecimal("120.00"), dto.getMontantDonne());
            assertEquals(new BigDecimal("20.00"), dto.getMonnaie());
            assertEquals(sampleDate(), dto.getDatePaiement());
            assertEquals(Paiement.ModePaiement.ESPECES, dto.getModePaiement());
            assertEquals("REF-410", dto.getReference());
            assertEquals("Paiement comptoir", dto.getNote());
            assertEquals(Integer.valueOf(400), dto.getFactureId());
            assertEquals("FAC-2026-400", dto.getFactureNumero());
            assertEquals(Integer.valueOf(9), dto.getEntrepriseId());
            assertEquals("Ma societe", dto.getEntrepriseNom());
        }

        @Test
        @DisplayName("tolerant aux associations nulles")
        void shouldTolerateNullAssociations() {
            Paiement paiement = sampleEntity();
            paiement.setFacture(null);
            paiement.setEntreprise(null);

            PaiementDto dto = PaiementDto.fromEntity(paiement);

            assertNull(dto.getFactureId());
            assertNull(dto.getFactureNumero());
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
            assertNull(PaiementDto.toEntity(null));
        }

        @Test
        @DisplayName("copie tous les champs simples")
        void shouldMapFields() {
            Paiement paiement = PaiementDto.toEntity(sampleDto());

            assertEquals(Integer.valueOf(410), paiement.getId());
            assertEquals(new BigDecimal("100.00"), paiement.getMontant());
            assertEquals(new BigDecimal("120.00"), paiement.getMontantDonne());
            assertEquals(new BigDecimal("20.00"), paiement.getMonnaie());
            assertEquals(sampleDate(), paiement.getDatePaiement());
            assertEquals(Paiement.ModePaiement.ESPECES, paiement.getModePaiement());
            assertEquals("REF-410", paiement.getReference());
            assertEquals("Paiement comptoir", paiement.getNote());
        }

        @Test
        @DisplayName("ne reconstruit pas les associations facture / entreprise")
        void shouldNotRebuildAssociations() {
            PaiementDto dto = sampleDto();
            dto.setFactureId(400);
            dto.setFactureNumero("FAC-2026-400");
            dto.setEntrepriseId(9);

            Paiement paiement = PaiementDto.toEntity(dto);

            assertNull(paiement.getFacture());
            assertNull(paiement.getEntreprise());
        }
    }

    @Test
    @DisplayName("aller-retour DTO -> entite -> DTO conserve les donnees")
    void roundTripShouldPreserveData() {
        PaiementDto original = sampleDto();
        PaiementDto result = PaiementDto.fromEntity(PaiementDto.toEntity(original));

        assertEquals(original, result);
    }
}
