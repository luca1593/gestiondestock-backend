package com.devtech.gestiondestock.validator;

import com.devtech.gestiondestock.dto.ClientDto;
import com.devtech.gestiondestock.dto.CommandeClientDto;
import com.devtech.gestiondestock.dto.LigneCommandeClientDto;
import com.devtech.gestiondestock.model.EtatCommande;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests unitaires du validateur de commande client.
 *
 * Champs obligatoires : code, dateCommande, etatcommande, client (avec id)
 * et au moins une ligne de commande.
 */
class CommandeClientValidatorTest {

    private CommandeClientDto.CommandeClientDtoBuilder validCommande() {
        return CommandeClientDto.builder()
                .code("CC-001")
                .dateCommande(Instant.parse("2026-01-01T10:00:00Z"))
                .etatcommande(EtatCommande.EN_PREPARATION)
                .client(ClientDto.builder().id(1).build())
                .ligneCommandeClients(Collections.singletonList(
                        LigneCommandeClientDto.builder().quantite(BigDecimal.ONE).build()));
    }

    @Test
    @DisplayName("Commande null : les 5 messages obligatoires")
    void shouldRejectNullCommande() {
        List<String> errors = CommandeClientValidator.validate(null);

        assertFalse(errors.isEmpty());
        assertEquals(5, errors.size());
        assertTrue(errors.contains("Veuillez renseigner le code de la commande"));
        assertTrue(errors.contains("Veuillez renseigner la date de la commande"));
        assertTrue(errors.contains("Veuillez renseigner l'etat de la commande"));
        assertTrue(errors.contains("Veuillez renseigner le client pour la commande"));
        assertTrue(errors.contains("Veuillez ajouter au moins un article pour la commande"));
    }

    @Test
    @DisplayName("Commande complete : aucune erreur")
    void shouldAcceptValidCommande() {
        assertTrue(CommandeClientValidator.validate(validCommande().build()).isEmpty());
    }

    @Test
    @DisplayName("Code vide : une seule erreur")
    void shouldRejectMissingCode() {
        List<String> errors = CommandeClientValidator.validate(validCommande().code("").build());

        assertEquals(1, errors.size());
        assertEquals("Veuillez renseigner le code de la commande", errors.get(0));
    }

    @Test
    @DisplayName("Date absente : une seule erreur")
    void shouldRejectMissingDate() {
        List<String> errors = CommandeClientValidator.validate(validCommande().dateCommande(null).build());

        assertEquals(1, errors.size());
        assertEquals("Veuillez renseigner la date de la commande", errors.get(0));
    }

    @Test
    @DisplayName("Etat absent : une seule erreur")
    void shouldRejectMissingEtat() {
        List<String> errors = CommandeClientValidator.validate(validCommande().etatcommande(null).build());

        assertEquals(1, errors.size());
        assertEquals("Veuillez renseigner l'etat de la commande", errors.get(0));
    }

    @Test
    @DisplayName("Client absent : une seule erreur")
    void shouldRejectMissingClient() {
        List<String> errors = CommandeClientValidator.validate(validCommande().client(null).build());

        assertEquals(1, errors.size());
        assertEquals("Veuillez renseigner le client pour la commande", errors.get(0));
    }

    @Test
    @DisplayName("Client sans id : une seule erreur")
    void shouldRejectClientWithoutId() {
        List<String> errors = CommandeClientValidator.validate(
                validCommande().client(ClientDto.builder().nom("Dupont").build()).build());

        assertEquals(1, errors.size());
        assertEquals("Veuillez renseigner le client pour la commande", errors.get(0));
    }

    @Test
    @DisplayName("Aucune ligne de commande : une seule erreur")
    void shouldRejectEmptyLignes() {
        List<String> errors = CommandeClientValidator.validate(
                validCommande().ligneCommandeClients(Collections.emptyList()).build());

        assertEquals(1, errors.size());
        assertEquals("Veuillez ajouter au moins un article pour la commande", errors.get(0));
    }
}
