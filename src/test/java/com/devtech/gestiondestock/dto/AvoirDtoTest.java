package com.devtech.gestiondestock.dto;

import com.devtech.gestiondestock.model.Avoir;
import com.devtech.gestiondestock.model.Client;
import com.devtech.gestiondestock.model.Vente;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Tests unitaires de la conversion DTO <-> entite pour {@link AvoirDto}.
 */
class AvoirDtoTest {

    private Instant sampleDate() {
        return Instant.parse("2026-10-16T11:00:00Z");
    }

    private ClientDto sampleClientDto() {
        return ClientDto.builder().id(3).nom("Dupont").prenom("Jean").build();
    }

    private Client sampleClient() {
        Client client = new Client();
        client.setId(3);
        client.setNom("Dupont");
        client.setPrenom("Jean");
        return client;
    }

    private VenteDto sampleVenteDto() {
        return VenteDto.builder().id(11).code("VTE-011").dateVente(sampleDate()).build();
    }

    private Vente sampleVente() {
        Vente vente = new Vente();
        vente.setId(11);
        vente.setCode("VTE-011");
        vente.setDateVente(sampleDate());
        return vente;
    }

    private AvoirDto sampleDto() {
        return AvoirDto.builder()
                .id(30)
                .code("AV-030")
                .dateAvoir(sampleDate())
                .montant(59.9)
                .raison("Article defectueux")
                .etat("VALIDE")
                .client(sampleClientDto())
                .vente(sampleVenteDto())
                .identreprise(9)
                .build();
    }

    private Avoir sampleEntity() {
        Avoir avoir = new Avoir();
        avoir.setId(30);
        avoir.setCode("AV-030");
        avoir.setDateAvoir(sampleDate());
        avoir.setMontant(59.9);
        avoir.setRaison("Article defectueux");
        avoir.setEtat("VALIDE");
        avoir.setClient(sampleClient());
        avoir.setVente(sampleVente());
        avoir.setIdentreprise(9);
        return avoir;
    }

    @Nested
    @DisplayName("fromEntity")
    class FromEntity {

        @Test
        @DisplayName("null renvoie null (pas de NullPointerException)")
        void shouldReturnNullForNullEntity() {
            assertNull(AvoirDto.fromEntity(null));
        }

        @Test
        @DisplayName("copie tous les champs et imbrique client / vente")
        void shouldMapAllFields() {
            AvoirDto dto = AvoirDto.fromEntity(sampleEntity());

            assertEquals(Integer.valueOf(30), dto.getId());
            assertEquals("AV-030", dto.getCode());
            assertEquals(sampleDate(), dto.getDateAvoir());
            assertEquals(Double.valueOf(59.9), dto.getMontant());
            assertEquals("Article defectueux", dto.getRaison());
            assertEquals("VALIDE", dto.getEtat());
            assertEquals(Integer.valueOf(9), dto.getIdentreprise());
            assertNotNull(dto.getClient());
            assertEquals("Dupont", dto.getClient().getNom());
            assertNotNull(dto.getVente());
            assertEquals("VTE-011", dto.getVente().getCode());
        }

        @Test
        @DisplayName("tolerant au client et a la vente nuls")
        void shouldTolerateNullAssociations() {
            Avoir avoir = sampleEntity();
            avoir.setClient(null);
            avoir.setVente(null);

            AvoirDto dto = AvoirDto.fromEntity(avoir);

            assertNull(dto.getClient());
            assertNull(dto.getVente());
        }
    }

    @Nested
    @DisplayName("toEntity")
    class ToEntity {

        @Test
        @DisplayName("null renvoie null (pas de NullPointerException)")
        void shouldReturnNullForNullDto() {
            assertNull(AvoirDto.toEntity(null));
        }

        @Test
        @DisplayName("copie tous les champs et imbrique client / vente")
        void shouldMapAllFields() {
            Avoir avoir = AvoirDto.toEntity(sampleDto());

            assertEquals(Integer.valueOf(30), avoir.getId());
            assertEquals("AV-030", avoir.getCode());
            assertEquals(sampleDate(), avoir.getDateAvoir());
            assertEquals(Double.valueOf(59.9), avoir.getMontant());
            assertEquals("Article defectueux", avoir.getRaison());
            assertEquals("VALIDE", avoir.getEtat());
            assertEquals(Integer.valueOf(9), avoir.getIdentreprise());
            assertNotNull(avoir.getClient());
            assertEquals("Dupont", avoir.getClient().getNom());
            assertNotNull(avoir.getVente());
            assertEquals("VTE-011", avoir.getVente().getCode());
        }
    }

    @Test
    @DisplayName("aller-retour DTO -> entite -> DTO conserve les donnees")
    void roundTripShouldPreserveData() {
        AvoirDto original = sampleDto();
        AvoirDto result = AvoirDto.fromEntity(AvoirDto.toEntity(original));

        assertEquals(original, result);
    }
}
