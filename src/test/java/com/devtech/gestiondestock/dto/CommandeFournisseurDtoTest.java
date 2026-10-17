package com.devtech.gestiondestock.dto;

import com.devtech.gestiondestock.model.CommandeFournisseur;
import com.devtech.gestiondestock.model.EtatCommande;
import com.devtech.gestiondestock.model.Fournisseur;
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
 * Tests unitaires de la conversion DTO <-> entite pour {@link CommandeFournisseurDto}.
 */
class CommandeFournisseurDtoTest {

    private Instant sampleDate() {
        return Instant.parse("2026-10-14T08:15:00Z");
    }

    private FournisseurDto sampleFournisseurDto() {
        return FournisseurDto.builder().id(8).nom("Martin").prenom("Paul").build();
    }

    private Fournisseur sampleFournisseur() {
        Fournisseur fournisseur = new Fournisseur();
        fournisseur.setId(8);
        fournisseur.setNom("Martin");
        fournisseur.setPrenom("Paul");
        return fournisseur;
    }

    private CommandeFournisseurDto sampleDto() {
        return CommandeFournisseurDto.builder()
                .id(60)
                .code("CF-060")
                .dateCommande(sampleDate())
                .etatcommande(EtatCommande.EN_PREPARATION)
                .fournisseur(sampleFournisseurDto())
                .identreprise(9)
                .build();
    }

    private CommandeFournisseur sampleEntity() {
        CommandeFournisseur commande = new CommandeFournisseur();
        commande.setId(60);
        commande.setCode("CF-060");
        commande.setDateCommande(sampleDate());
        commande.setEtatcommande(EtatCommande.EN_PREPARATION);
        commande.setFournisseur(sampleFournisseur());
        commande.setIdentreprise(9);
        return commande;
    }

    @Nested
    @DisplayName("fromEntity")
    class FromEntity {

        @Test
        @DisplayName("null renvoie null (pas de NullPointerException)")
        void shouldReturnNullForNullEntity() {
            assertNull(CommandeFournisseurDto.fromEntity(null));
        }

        @Test
        @DisplayName("copie tous les champs et imbrique le fournisseur")
        void shouldMapAllFields() {
            CommandeFournisseurDto dto = CommandeFournisseurDto.fromEntity(sampleEntity());

            assertEquals(Integer.valueOf(60), dto.getId());
            assertEquals("CF-060", dto.getCode());
            assertEquals(sampleDate(), dto.getDateCommande());
            assertEquals(EtatCommande.EN_PREPARATION, dto.getEtatcommande());
            assertEquals(Integer.valueOf(9), dto.getIdentreprise());
            assertNotNull(dto.getFournisseur());
            assertEquals("Martin", dto.getFournisseur().getNom());
        }

        @Test
        @DisplayName("ne mappe pas la liste ligneCommandeFournisseurs")
        void shouldNotMapLignes() {
            assertNull(CommandeFournisseurDto.fromEntity(sampleEntity()).getLigneCommandeFournisseurs());
        }
    }

    @Nested
    @DisplayName("toEntity")
    class ToEntity {

        @Test
        @DisplayName("null renvoie null (pas de NullPointerException)")
        void shouldReturnNullForNullDto() {
            assertNull(CommandeFournisseurDto.toEntity(null));
        }

        @Test
        @DisplayName("copie tous les champs et imbrique le fournisseur")
        void shouldMapAllFields() {
            CommandeFournisseur commande = CommandeFournisseurDto.toEntity(sampleDto());

            assertEquals(Integer.valueOf(60), commande.getId());
            assertEquals("CF-060", commande.getCode());
            assertEquals(sampleDate(), commande.getDateCommande());
            assertEquals(EtatCommande.EN_PREPARATION, commande.getEtatcommande());
            assertEquals(Integer.valueOf(9), commande.getIdentreprise());
            assertNotNull(commande.getFournisseur());
            assertEquals("Martin", commande.getFournisseur().getNom());
        }
    }

    @Nested
    @DisplayName("isCommandeLivree")
    class IsCommandeLivree {

        @Test
        @DisplayName("vrai lorsque l'etat est LIVREE")
        void shouldBeTrueWhenLivree() {
            CommandeFournisseurDto dto = sampleDto();
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
            CommandeFournisseurDto dto = sampleDto();
            dto.setEtatcommande(null);

            assertFalse(dto.isCommandeLivree());
        }
    }

    @Test
    @DisplayName("aller-retour DTO -> entite -> DTO conserve les donnees")
    void roundTripShouldPreserveData() {
        CommandeFournisseurDto original = sampleDto();
        CommandeFournisseurDto result =
                CommandeFournisseurDto.fromEntity(CommandeFournisseurDto.toEntity(original));

        assertEquals(original, result);
    }
}
