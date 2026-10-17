package com.devtech.gestiondestock.dto;

import com.devtech.gestiondestock.model.Client;
import com.devtech.gestiondestock.model.CommandeClient;
import com.devtech.gestiondestock.model.EtatCommande;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests unitaires de la conversion DTO <-> entite pour {@link CommandeClientDto}.
 */
class CommandeClientDtoTest {

    private Instant sampleDate() {
        return Instant.parse("2026-10-14T08:15:00Z");
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

    private CommandeClientDto sampleDto() {
        return CommandeClientDto.builder()
                .id(50)
                .code("CC-050")
                .dateCommande(sampleDate())
                .etatcommande(EtatCommande.EN_PREPARATION)
                .client(sampleClientDto())
                .identreprise(9)
                .build();
    }

    private CommandeClient sampleEntity() {
        CommandeClient commande = new CommandeClient();
        commande.setId(50);
        commande.setCode("CC-050");
        commande.setDateCommande(sampleDate());
        commande.setEtatcommande(EtatCommande.EN_PREPARATION);
        commande.setClient(sampleClient());
        commande.setIdentreprise(9);
        return commande;
    }

    @Nested
    @DisplayName("fromEntity")
    class FromEntity {

        @Test
        @DisplayName("null renvoie null (pas de NullPointerException)")
        void shouldReturnNullForNullEntity() {
            assertNull(CommandeClientDto.fromEntity(null));
        }

        @Test
        @DisplayName("copie tous les champs et imbrique le client")
        void shouldMapAllFields() {
            CommandeClientDto dto = CommandeClientDto.fromEntity(sampleEntity());

            assertEquals(Integer.valueOf(50), dto.getId());
            assertEquals("CC-050", dto.getCode());
            assertEquals(sampleDate(), dto.getDateCommande());
            assertEquals(EtatCommande.EN_PREPARATION, dto.getEtatcommande());
            assertEquals(Integer.valueOf(9), dto.getIdentreprise());
            assertNotNull(dto.getClient());
            assertEquals("Dupont", dto.getClient().getNom());
        }

        @Test
        @DisplayName("ne mappe pas la liste ligneCommandeClients")
        void shouldNotMapLignes() {
            assertNull(CommandeClientDto.fromEntity(sampleEntity()).getLigneCommandeClients());
        }
    }

    @Nested
    @DisplayName("toEntity")
    class ToEntity {

        @Test
        @DisplayName("null renvoie null (pas de NullPointerException)")
        void shouldReturnNullForNullDto() {
            assertNull(CommandeClientDto.toEntity(null));
        }

        @Test
        @DisplayName("copie tous les champs et imbrique le client")
        void shouldMapAllFields() {
            CommandeClient commande = CommandeClientDto.toEntity(sampleDto());

            assertEquals(Integer.valueOf(50), commande.getId());
            assertEquals("CC-050", commande.getCode());
            assertEquals(sampleDate(), commande.getDateCommande());
            assertEquals(EtatCommande.EN_PREPARATION, commande.getEtatcommande());
            assertEquals(Integer.valueOf(9), commande.getIdentreprise());
            assertNotNull(commande.getClient());
            assertEquals("Dupont", commande.getClient().getNom());
        }
    }

    @Nested
    @DisplayName("isCommandeLivree")
    class IsCommandeLivree {

        @Test
        @DisplayName("vrai lorsque l'etat est LIVREE")
        void shouldBeTrueWhenLivree() {
            CommandeClientDto dto = sampleDto();
            dto.setEtatcommande(EtatCommande.LIVREE);

            assertTrue(dto.isCommandeLivree());
        }

        @Test
        @DisplayName("faux lorsque l'etat n'est pas LIVREE")
        void shouldBeFalseForOtherStates() {
            assertFalse(sampleDto().isCommandeLivree());
        }

        @Test
        @DisplayName("faux lorsque l'etat est nul")
        void shouldBeFalseWhenNull() {
            CommandeClientDto dto = sampleDto();
            dto.setEtatcommande(null);

            assertFalse(dto.isCommandeLivree());
        }
    }

    @Test
    @DisplayName("aller-retour DTO -> entite -> DTO conserve les donnees")
    void roundTripShouldPreserveData() {
        CommandeClientDto original = sampleDto();
        CommandeClientDto result = CommandeClientDto.fromEntity(CommandeClientDto.toEntity(original));

        assertEquals(original, result);
    }
}
