package com.devtech.gestiondestock.validator;

import com.devtech.gestiondestock.dto.CommandeFournisseurDto;
import com.devtech.gestiondestock.dto.FournisseurDto;
import com.devtech.gestiondestock.dto.LigneCommandeFournisseurDto;
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
 * Tests unitaires du validateur de commande fournisseur.
 *
 * Champs obligatoires : code, dateCommande, etatcommande, fournisseur (avec id)
 * et au moins une ligne de commande.
 */
class CommandeFournisseurValidatorTest {

    private CommandeFournisseurDto.CommandeFournisseurDtoBuilder validCommande() {
        return CommandeFournisseurDto.builder()
                .code("CF-001")
                .dateCommande(Instant.parse("2026-01-01T10:00:00Z"))
                .etatcommande(EtatCommande.EN_PREPARATION)
                .fournisseur(FournisseurDto.builder().id(1).build())
                .ligneCommandeFournisseurs(Collections.singletonList(
                        LigneCommandeFournisseurDto.builder().quantite(BigDecimal.ONE).build()));
    }

    @Test
    @DisplayName("Commande null : les 5 messages obligatoires")
    void shouldRejectNullCommande() {
        List<String> errors = CommandeFournisseurValidator.validate(null);

        assertFalse(errors.isEmpty());
        assertEquals(5, errors.size());
        assertTrue(errors.contains("Veuillez renseigner le code de la commande"));
        assertTrue(errors.contains("Veuillez renseigner la date de la commande"));
        assertTrue(errors.contains("Veuillez renseigner l'etat de la commande"));
        assertTrue(errors.contains("Veuillez renseigner le fournisseur pour la commande"));
        assertTrue(errors.contains("Veuillez ajouter au moins un article pour la commande"));
    }

    @Test
    @DisplayName("Commande complete : aucune erreur")
    void shouldAcceptValidCommande() {
        assertTrue(CommandeFournisseurValidator.validate(validCommande().build()).isEmpty());
    }

    @Test
    @DisplayName("Code vide : une seule erreur")
    void shouldRejectMissingCode() {
        List<String> errors = CommandeFournisseurValidator.validate(validCommande().code("").build());

        assertEquals(1, errors.size());
        assertEquals("Veuillez renseigner le code de la commande", errors.get(0));
    }

    @Test
    @DisplayName("Date absente : une seule erreur")
    void shouldRejectMissingDate() {
        List<String> errors = CommandeFournisseurValidator.validate(validCommande().dateCommande(null).build());

        assertEquals(1, errors.size());
        assertEquals("Veuillez renseigner la date de la commande", errors.get(0));
    }

    @Test
    @DisplayName("Etat absent : une seule erreur")
    void shouldRejectMissingEtat() {
        List<String> errors = CommandeFournisseurValidator.validate(validCommande().etatcommande(null).build());

        assertEquals(1, errors.size());
        assertEquals("Veuillez renseigner l'etat de la commande", errors.get(0));
    }

    @Test
    @DisplayName("Fournisseur absent : une seule erreur")
    void shouldRejectMissingFournisseur() {
        List<String> errors = CommandeFournisseurValidator.validate(validCommande().fournisseur(null).build());

        assertEquals(1, errors.size());
        assertEquals("Veuillez renseigner le fournisseur pour la commande", errors.get(0));
    }

    @Test
    @DisplayName("Fournisseur sans id : une seule erreur")
    void shouldRejectFournisseurWithoutId() {
        List<String> errors = CommandeFournisseurValidator.validate(
                validCommande().fournisseur(FournisseurDto.builder().nom("Fourn").build()).build());

        assertEquals(1, errors.size());
        assertEquals("Veuillez renseigner le fournisseur pour la commande", errors.get(0));
    }

    @Test
    @DisplayName("Aucune ligne de commande : une seule erreur")
    void shouldRejectEmptyLignes() {
        List<String> errors = CommandeFournisseurValidator.validate(
                validCommande().ligneCommandeFournisseurs(Collections.emptyList()).build());

        assertEquals(1, errors.size());
        assertEquals("Veuillez ajouter au moins un article pour la commande", errors.get(0));
    }
}
