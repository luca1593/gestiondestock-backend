package com.devtech.gestiondestock.validator;

import com.devtech.gestiondestock.dto.AdresseDto;
import com.devtech.gestiondestock.dto.ClientDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests unitaires du validateur de client.
 *
 * Champs obligatoires : nom, prenom, email, numTel, plus une adresse valide
 * (deleguee a AdresseValidator).
 */
class ClientValidatorTest {

    private AdresseDto validAdresse() {
        return AdresseDto.builder()
                .adresse1("12 rue des Lilas")
                .ville("Paris")
                .codePostal("75001")
                .pays("France")
                .build();
    }

    private ClientDto.ClientDtoBuilder validClient() {
        return ClientDto.builder()
                .nom("Dupont")
                .prenom("Jean")
                .email("jean.dupont@mail.com")
                .numTel("0612345678")
                .adresse(validAdresse());
    }

    @Test
    @DisplayName("Client null : 4 messages client + 5 messages d'adresse")
    void shouldRejectNullClient() {
        List<String> errors = ClientValidator.validate(null);

        assertFalse(errors.isEmpty());
        assertEquals(9, errors.size());
        assertTrue(errors.contains("Veuillez renseigner le nom du client"));
        assertTrue(errors.contains("Veuillez renseigner le prenom du client"));
        assertTrue(errors.contains("Veuillez renseigner le Mail du client"));
        assertTrue(errors.contains("Veuillez renseigner le numero de telephone du client"));
        assertTrue(errors.contains("Veuillez renseigner un addresse valide"));
    }

    @Test
    @DisplayName("Client complet : aucune erreur")
    void shouldAcceptValidClient() {
        assertTrue(ClientValidator.validate(validClient().build()).isEmpty());
    }

    @Test
    @DisplayName("Nom vide : une seule erreur")
    void shouldRejectMissingNom() {
        List<String> errors = ClientValidator.validate(validClient().nom("").build());

        assertEquals(1, errors.size());
        assertEquals("Veuillez renseigner le nom du client", errors.get(0));
    }

    @Test
    @DisplayName("Prenom vide : une seule erreur")
    void shouldRejectMissingPrenom() {
        List<String> errors = ClientValidator.validate(validClient().prenom("").build());

        assertEquals(1, errors.size());
        assertEquals("Veuillez renseigner le prenom du client", errors.get(0));
    }

    @Test
    @DisplayName("Email vide : une seule erreur")
    void shouldRejectMissingEmail() {
        List<String> errors = ClientValidator.validate(validClient().email("").build());

        assertEquals(1, errors.size());
        assertEquals("Veuillez renseigner le Mail du client", errors.get(0));
    }

    @Test
    @DisplayName("Numero de telephone vide : une seule erreur")
    void shouldRejectMissingNumTel() {
        List<String> errors = ClientValidator.validate(validClient().numTel("").build());

        assertEquals(1, errors.size());
        assertEquals("Veuillez renseigner le numero de telephone du client", errors.get(0));
    }

    @Test
    @DisplayName("Adresse absente : 5 erreurs d'adresse")
    void shouldRejectMissingAdresse() {
        List<String> errors = ClientValidator.validate(validClient().adresse(null).build());

        assertEquals(5, errors.size());
        assertTrue(errors.contains("L'adresse 1 est obligatoire"));
        assertTrue(errors.contains("La ville est obligatoire"));
    }
}
