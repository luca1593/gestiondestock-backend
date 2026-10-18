package com.devtech.gestiondestock.validator;

import com.devtech.gestiondestock.dto.AdresseDto;
import com.devtech.gestiondestock.dto.EntrepriseDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests unitaires du validateur d'entreprise.
 *
 * Champs obligatoires : nom, description, codeFiscal, email, numTel,
 * plus une adresse valide (deleguee a AdresseValidator).
 */
class EntrepriseValidatorTest {

    private AdresseDto validAdresse() {
        return AdresseDto.builder()
                .adresse1("12 rue des Lilas")
                .ville("Paris")
                .codePostal("75001")
                .pays("France")
                .build();
    }

    private EntrepriseDto.EntrepriseDtoBuilder validEntreprise() {
        return EntrepriseDto.builder()
                .nom("DevTech")
                .description("Editeur de logiciels")
                .codeFiscal("FR123456789")
                .email("contact@devtech.com")
                .numTel("0123456789")
                .adresse(validAdresse());
    }

    @Test
    @DisplayName("Entreprise null : 6 messages entreprise + 5 messages d'adresse")
    void shouldRejectNullEntreprise() {
        List<String> errors = EntrepriseValidator.validate(null);

        assertFalse(errors.isEmpty());
        assertEquals(11, errors.size());
        assertTrue(errors.contains("Veuillez renseigner le nom de l'entreprise"));
        assertTrue(errors.contains("Veuillez renseigner la description de l'entreprise"));
        assertTrue(errors.contains("Veuillez renseigner le code fiscal de l'entreprise"));
        assertTrue(errors.contains("Veuillez renseigner l'adresse de l'entreprise"));
        assertTrue(errors.contains("Veuillez renseigner le Mail de l'entreprise"));
        assertTrue(errors.contains("Veuillez renseigner le numero de telephone de l'entreprise"));
    }

    @Test
    @DisplayName("Entreprise complete : aucune erreur")
    void shouldAcceptValidEntreprise() {
        assertTrue(EntrepriseValidator.validate(validEntreprise().build()).isEmpty());
    }

    @Test
    @DisplayName("Nom vide : une seule erreur")
    void shouldRejectMissingNom() {
        List<String> errors = EntrepriseValidator.validate(validEntreprise().nom("").build());

        assertEquals(1, errors.size());
        assertEquals("Veuillez renseigner le nom de l'entreprise", errors.get(0));
    }

    @Test
    @DisplayName("Description vide : une seule erreur")
    void shouldRejectMissingDescription() {
        List<String> errors = EntrepriseValidator.validate(validEntreprise().description("").build());

        assertEquals(1, errors.size());
        assertEquals("Veuillez renseigner la description de l'entreprise", errors.get(0));
    }

    @Test
    @DisplayName("Code fiscal vide : une seule erreur")
    void shouldRejectMissingCodeFiscal() {
        List<String> errors = EntrepriseValidator.validate(validEntreprise().codeFiscal("").build());

        assertEquals(1, errors.size());
        assertEquals("Veuillez renseigner le code fiscal de l'entreprise", errors.get(0));
    }

    @Test
    @DisplayName("Email vide : une seule erreur")
    void shouldRejectMissingEmail() {
        List<String> errors = EntrepriseValidator.validate(validEntreprise().email("").build());

        assertEquals(1, errors.size());
        assertEquals("Veuillez renseigner le Mail de l'entreprise", errors.get(0));
    }

    @Test
    @DisplayName("Numero de telephone vide : une seule erreur")
    void shouldRejectMissingNumTel() {
        List<String> errors = EntrepriseValidator.validate(validEntreprise().numTel("").build());

        assertEquals(1, errors.size());
        assertEquals("Veuillez renseigner le numero de telephone de l'entreprise", errors.get(0));
    }

    @Test
    @DisplayName("Adresse absente : 5 erreurs d'adresse (pas le message 'adresse de l'entreprise')")
    void shouldRejectMissingAdresse() {
        List<String> errors = EntrepriseValidator.validate(validEntreprise().adresse(null).build());

        assertEquals(5, errors.size());
        assertTrue(errors.contains("L'adresse 1 est obligatoire"));
        assertTrue(errors.contains("La ville est obligatoire"));
        // Le message "Veuillez renseigner l'adresse de l'entreprise" n'apparait que dans la branche null.
        assertFalse(errors.contains("Veuillez renseigner l'adresse de l'entreprise"));
    }
}
