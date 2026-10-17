package com.devtech.gestiondestock.dto;

import com.devtech.gestiondestock.model.Entreprise;
import com.devtech.gestiondestock.model.Entrepot;
import com.devtech.gestiondestock.model.TransfertStock;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Tests unitaires de la conversion DTO <-> entite pour {@link TransfertStockDto}.
 *
 * Particularite : fromEntity derive entreprise et entrepots (ids + noms),
 * mais toEntity ne reconstruit pas ces associations.
 */
class TransfertStockDtoTest {

    private LocalDateTime sampleDate() {
        return LocalDateTime.of(2026, 10, 16, 9, 45);
    }

    private Entreprise sampleEntreprise() {
        Entreprise entreprise = new Entreprise();
        entreprise.setId(9);
        entreprise.setNom("Ma societe");
        return entreprise;
    }

    private Entrepot sampleEntrepotSource() {
        Entrepot entrepot = new Entrepot();
        entrepot.setId(5);
        entrepot.setNom("Entrepot source");
        return entrepot;
    }

    private Entrepot sampleEntrepotDestination() {
        Entrepot entrepot = new Entrepot();
        entrepot.setId(6);
        entrepot.setNom("Entrepot destination");
        return entrepot;
    }

    private TransfertStockDto sampleDto() {
        return TransfertStockDto.builder()
                .id(600)
                .code("TRF-600")
                .dateTransfert(sampleDate())
                .statut(TransfertStock.StatutTransfert.EN_ATTENTE)
                .build();
    }

    private TransfertStock sampleEntity() {
        TransfertStock transfert = new TransfertStock();
        transfert.setId(600);
        transfert.setCode("TRF-600");
        transfert.setDateTransfert(sampleDate());
        transfert.setStatut(TransfertStock.StatutTransfert.EN_ATTENTE);
        transfert.setEntreprise(sampleEntreprise());
        transfert.setEntrepotSource(sampleEntrepotSource());
        transfert.setEntrepotDestination(sampleEntrepotDestination());
        return transfert;
    }

    @Nested
    @DisplayName("fromEntity")
    class FromEntity {

        @Test
        @DisplayName("null renvoie null (pas de NullPointerException)")
        void shouldReturnNullForNullEntity() {
            assertNull(TransfertStockDto.fromEntity(null));
        }

        @Test
        @DisplayName("copie tous les champs simples et derive les associations")
        void shouldMapAllFields() {
            TransfertStockDto dto = TransfertStockDto.fromEntity(sampleEntity());

            assertEquals(Integer.valueOf(600), dto.getId());
            assertEquals("TRF-600", dto.getCode());
            assertEquals(sampleDate(), dto.getDateTransfert());
            assertEquals(TransfertStock.StatutTransfert.EN_ATTENTE, dto.getStatut());
            assertEquals(Integer.valueOf(9), dto.getEntrepriseId());
            assertEquals("Ma societe", dto.getEntrepriseNom());
            assertEquals(Integer.valueOf(5), dto.getEntrepotSourceId());
            assertEquals("Entrepot source", dto.getEntrepotSourceNom());
            assertEquals(Integer.valueOf(6), dto.getEntrepotDestinationId());
            assertEquals("Entrepot destination", dto.getEntrepotDestinationNom());
        }

        @Test
        @DisplayName("tolerant aux associations nulles")
        void shouldTolerateNullAssociations() {
            TransfertStock transfert = sampleEntity();
            transfert.setEntreprise(null);
            transfert.setEntrepotSource(null);
            transfert.setEntrepotDestination(null);

            TransfertStockDto dto = TransfertStockDto.fromEntity(transfert);

            assertNull(dto.getEntrepriseId());
            assertNull(dto.getEntrepriseNom());
            assertNull(dto.getEntrepotSourceId());
            assertNull(dto.getEntrepotSourceNom());
            assertNull(dto.getEntrepotDestinationId());
            assertNull(dto.getEntrepotDestinationNom());
        }
    }

    @Nested
    @DisplayName("toEntity")
    class ToEntity {

        @Test
        @DisplayName("null renvoie null (pas de NullPointerException)")
        void shouldReturnNullForNullDto() {
            assertNull(TransfertStockDto.toEntity(null));
        }

        @Test
        @DisplayName("copie tous les champs simples")
        void shouldMapFields() {
            TransfertStock transfert = TransfertStockDto.toEntity(sampleDto());

            assertEquals(Integer.valueOf(600), transfert.getId());
            assertEquals("TRF-600", transfert.getCode());
            assertEquals(sampleDate(), transfert.getDateTransfert());
            assertEquals(TransfertStock.StatutTransfert.EN_ATTENTE, transfert.getStatut());
        }

        @Test
        @DisplayName("ne reconstruit pas les associations entreprise / entrepots")
        void shouldNotRebuildAssociations() {
            TransfertStockDto dto = sampleDto();
            dto.setEntrepriseId(9);
            dto.setEntrepotSourceId(5);
            dto.setEntrepotDestinationId(6);

            TransfertStock transfert = TransfertStockDto.toEntity(dto);

            assertNull(transfert.getEntreprise());
            assertNull(transfert.getEntrepotSource());
            assertNull(transfert.getEntrepotDestination());
        }
    }

    @Test
    @DisplayName("aller-retour DTO -> entite -> DTO conserve les donnees")
    void roundTripShouldPreserveData() {
        TransfertStockDto original = sampleDto();
        TransfertStockDto result = TransfertStockDto.fromEntity(TransfertStockDto.toEntity(original));

        assertEquals(original, result);
    }
}
