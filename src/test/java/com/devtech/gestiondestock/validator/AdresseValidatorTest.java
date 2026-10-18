package com.devtech.gestiondestock.validator;

import com.devtech.gestiondestock.dto.AdresseDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests unitaires du validateur d'adresse.
 *
 * Le validateur ajoute un message general lorsque l'objet est null,
 * puis un message par champ obligatoire (adresse1, ville, code postal, pays).
 * adresse2 reste facultative.
 */
class AdresseValidatorTest {

    private AdresseDto.AdresseDtoBuilder validAdresse() {
        return AdresseDto.builder()
                .adresse1("12 rue des Lilas")
                .ville("Paris")
                .codePostal("75001")
                .pays("France");
    }

    @Test
    @DisplayName("Adresse null : un message general plus un message par champ obligatoire")
    void shouldRejectNullAdresse() {
        List<String> errors = AdresseValidator.validate(null);

        assertFalse(errors.isEmpty());
        assertEquals(5, errors.size(), "Une adresse null doit produire 5 messages");
        assertTrue(errors.contains("Veuillez renseigner un addresse valide"));
        assertTrue(errors.contains("L'adresse 1 est obligatoire"));
        assertTrue(errors.contains("La ville est obligatoire"));
        assertTrue(errors.contains("Le code postal est obligatoire"));
        assertTrue(errors.contains("Le pays est obligatoire"));
    }

    @Test
    @DisplayName("Adresse complete : aucune erreur")
    void shouldAcceptValidAdresse() {
        assertTrue(AdresseValidator.validate(validAdresse().build()).isEmpty());
    }

    @Test
    @DisplayName("Adresse 1 vide : une seule erreur")
    void shouldRejectMissingAdresse1() {
        List<String> errors = AdresseValidator.validate(validAdresse().adresse1("").build());

        assertEquals(1, errors.size());
        assertEquals("L'adresse 1 est obligatoire", errors.get(0));
    }

    @Test
    @DisplayName("Ville vide : une seule erreur")
    void shouldRejectMissingVille() {
        List<String> errors = AdresseValidator.validate(validAdresse().ville("").build());

        assertEquals(1, errors.size());
        assertEquals("La ville est obligatoire", errors.get(0));
    }

    @Test
    @DisplayName("Code postal vide : une seule erreur")
    void shouldRejectMissingCodePostal() {
        List<String> errors = AdresseValidator.validate(validAdresse().codePostal("").build());

        assertEquals(1, errors.size());
        assertEquals("Le code postal est obligatoire", errors.get(0));
    }

    @Test
    @DisplayName("Pays vide : une seule erreur")
    void shouldRejectMissingPays() {
        List<String> errors = AdresseValidator.validate(validAdresse().pays("").build());

        assertEquals(1, errors.size());
        assertEquals("Le pays est obligatoire", errors.get(0));
    }

    @Test
    @DisplayName("Adresse 1 composee uniquement d'espaces : acceptee (hasLength, pas hasText)")
    void shouldAcceptWhitespaceOnlyAdresse1() {
        // Comportement actuel verrouille : StringUtils.hasLength("   ") vaut true,
        // donc une chaine d'espaces n'est pas consideree comme vide par le validateur.
        assertTrue(AdresseValidator.validate(validAdresse().adresse1("   ").build()).isEmpty());
    }

    @Test
    @DisplayName("Adresse 2 absente : aucune erreur (champ facultatif)")
    void shouldAcceptMissingAdresse2() {
        assertTrue(AdresseValidator.validate(validAdresse().build()).isEmpty());
    }
}
