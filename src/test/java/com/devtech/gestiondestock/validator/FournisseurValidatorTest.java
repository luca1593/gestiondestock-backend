package com.devtech.gestiondestock.validator;

import com.devtech.gestiondestock.dto.AdresseDto;
import com.devtech.gestiondestock.dto.FournisseurDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests unitaires du validateur de fournisseur.
 *
 * Champs obligatoires : nom, prenom, email, numTel, plus une adresse valide
 * (deleguee a AdresseValidator).
 */
class FournisseurValidatorTest {

    private AdresseDto validAdresse() {
        return AdresseDto.builder()
                .adresse1("12 rue des Lilas")
                .ville("Paris")
                .codePostal("75001")
                .pays("France")
                .build();
    }

    private FournisseurDto.FournisseurDtoBuilder validFournisseur() {
        return FournisseurDto.builder()
                .nom("Martin")
                .prenom("Paul")
                .email("paul.martin@mail.com")
                .numTel("0612345678")
                .adresse(validAdresse());
    }

    @Test
    @DisplayName("Fournisseur null : 5 messages fournisseur + 5 messages d'adresse")
    void shouldRejectNullFournisseur() {
        List<String> errors = FournisseurValidator.validate(null);

        assertFalse(errors.isEmpty());
        assertEquals(10, errors.size());
        assertTrue(errors.contains("Veuillez renseigner le nom du fournisseur"));
        assertTrue(errors.contains("Veuillez renseigner le prenom du fournisseur"));
        assertTrue(errors.contains("Veuillez renseigner le Mail du fournisseur"));
        assertTrue(errors.contains("Veuillez renseigner l'adresse du fournisseur"));
        assertTrue(errors.contains("Veuillez renseigner le numero de telephone du fournisseur"));
    }

    @Test
    @DisplayName("Fournisseur complet : aucune erreur")
    void shouldAcceptValidFournisseur() {
        assertTrue(FournisseurValidator.validate(validFournisseur().build()).isEmpty());
    }

    @Test
    @DisplayName("Nom vide : une seule erreur")
    void shouldRejectMissingNom() {
        List<String> errors = FournisseurValidator.validate(validFournisseur().nom("").build());

        assertEquals(1, errors.size());
        assertEquals("Veuillez renseigner le nom du fournisseur", errors.get(0));
    }

    @Test
    @DisplayName("Prenom vide : une seule erreur")
    void shouldRejectMissingPrenom() {
        List<String> errors = FournisseurValidator.validate(validFournisseur().prenom("").build());

        assertEquals(1, errors.size());
        assertEquals("Veuillez renseigner le prenom du fournisseur", errors.get(0));
    }

    @Test
    @DisplayName("Email vide : une seule erreur")
    void shouldRejectMissingEmail() {
        List<String> errors = FournisseurValidator.validate(validFournisseur().email("").build());

        assertEquals(1, errors.size());
        assertEquals("Veuillez renseigner le Mail du fournisseur", errors.get(0));
    }

    @Test
    @DisplayName("Numero de telephone vide : une seule erreur")
    void shouldRejectMissingNumTel() {
        List<String> errors = FournisseurValidator.validate(validFournisseur().numTel("").build());

        assertEquals(1, errors.size());
        assertEquals("Veuillez renseigner le numero de telephone du fournisseur", errors.get(0));
    }

    @Test
    @DisplayName("Adresse absente : 5 erreurs d'adresse")
    void shouldRejectMissingAdresse() {
        List<String> errors = FournisseurValidator.validate(validFournisseur().adresse(null).build());

        assertEquals(5, errors.size());
        assertTrue(errors.contains("L'adresse 1 est obligatoire"));
        assertTrue(errors.contains("La ville est obligatoire"));
        // Le message "Veuillez renseigner l'adresse du fournisseur" n'apparait que dans la branche null.
        assertFalse(errors.contains("Veuillez renseigner l'adresse du fournisseur"));
    }
}
