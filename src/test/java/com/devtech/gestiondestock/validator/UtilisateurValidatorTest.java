package com.devtech.gestiondestock.validator;

import com.devtech.gestiondestock.dto.AdresseDto;
import com.devtech.gestiondestock.dto.UtilisateurDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests unitaires du validateur d'utilisateur.
 *
 * Champs reellement verifies : nom, prenom, dateDeNaissance et l'adresse complete.
 * L'email et le mot de passe ne sont PAS controles pour un DTO non null,
 * bien que la branche null mentionne le mot de passe et l'adresse.
 */
class UtilisateurValidatorTest {

    private AdresseDto validAdresse() {
        return AdresseDto.builder()
                .adresse1("12 rue des Lilas")
                .ville("Paris")
                .codePostal("75001")
                .pays("France")
                .build();
    }

    private UtilisateurDto.UtilisateurDtoBuilder validUtilisateur() {
        return UtilisateurDto.builder()
                .nom("Dupont")
                .prenom("Jean")
                .dateDeNaissance(Instant.parse("1990-05-15T00:00:00Z"))
                .adresse(validAdresse());
    }

    @Test
    @DisplayName("Utilisateur null : 5 messages utilisateur + 5 messages d'adresse")
    void shouldRejectNullUtilisateur() {
        List<String> errors = UtilisateurValidator.validate(null);

        assertFalse(errors.isEmpty());
        assertEquals(10, errors.size());
        assertTrue(errors.contains("Veuillez renseigner le nom d'utilisateur"));
        assertTrue(errors.contains("Veuillez renseigner le prenom d'utilisateur"));
        assertTrue(errors.contains("Veuillez renseigner le mot de passe d'utilisateur"));
        assertTrue(errors.contains("Veuillez renseigner l'adresse d'utilisateur"));
        assertTrue(errors.contains("Veuillez renseigner la date de naissance de l'utilisateur"));
    }

    @Test
    @DisplayName("Utilisateur complet : aucune erreur")
    void shouldAcceptValidUtilisateur() {
        assertTrue(UtilisateurValidator.validate(validUtilisateur().build()).isEmpty());
    }

    @Test
    @DisplayName("Nom vide : une seule erreur")
    void shouldRejectMissingNom() {
        List<String> errors = UtilisateurValidator.validate(validUtilisateur().nom("").build());

        assertEquals(1, errors.size());
        assertEquals("Veuillez renseigner le nom d'utilisateur", errors.get(0));
    }

    @Test
    @DisplayName("Prenom vide : une seule erreur")
    void shouldRejectMissingPrenom() {
        List<String> errors = UtilisateurValidator.validate(validUtilisateur().prenom("").build());

        assertEquals(1, errors.size());
        assertEquals("Veuillez renseigner le prenom d'utilisateur", errors.get(0));
    }

    @Test
    @DisplayName("Date de naissance absente : une seule erreur")
    void shouldRejectMissingDateDeNaissance() {
        List<String> errors = UtilisateurValidator.validate(validUtilisateur().dateDeNaissance(null).build());

        assertEquals(1, errors.size());
        assertEquals("Veuillez renseigner la date de naissance de l'utilisateur", errors.get(0));
    }

    @Test
    @DisplayName("Adresse absente : 5 erreurs d'adresse")
    void shouldRejectMissingAdresse() {
        List<String> errors = UtilisateurValidator.validate(validUtilisateur().adresse(null).build());

        assertEquals(5, errors.size());
        assertTrue(errors.contains("L'adresse 1 est obligatoire"));
        assertTrue(errors.contains("La ville est obligatoire"));
    }

    @Test
    @DisplayName("Email et mot de passe absents : aucune erreur (non verifies pour un DTO non null)")
    void shouldNotValidateEmailNorPassword() {
        // Comportement actuel verrouille : malgre les messages presents dans la branche null,
        // l'email et le mot de passe ne sont jamais controles sur un DTO non null.
        assertTrue(UtilisateurValidator.validate(
                validUtilisateur().email(null).motDePasse(null).build()).isEmpty());
    }
}
