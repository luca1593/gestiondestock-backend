package com.devtech.gestiondestock.validator;

import com.devtech.gestiondestock.dto.PaiementDto;
import com.devtech.gestiondestock.model.Paiement;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests unitaires du validateur de paiement.
 *
 * Champs obligatoires : montant, datePaiement, modePaiement, factureId.
 * Regles supplementaires selon le mode : montantDonne pour les especes,
 * reference pour MOBILE_MONEY / CARTE_BANCAIRE / VIREMENT_BANCAIRE / CHEQUE.
 */
class PaiementValidatorTest {

    private PaiementDto.PaiementDtoBuilder validPaiementEspeces() {
        return PaiementDto.builder()
                .montant(new BigDecimal("100.00"))
                .montantDonne(new BigDecimal("100.00"))
                .datePaiement(LocalDateTime.parse("2026-01-01T10:00:00"))
                .modePaiement(Paiement.ModePaiement.ESPECES)
                .factureId(1);
    }

    @Test
    @DisplayName("Paiement null : les 4 messages obligatoires")
    void shouldRejectNullPaiement() {
        List<String> errors = PaiementValidator.validate(null);

        assertFalse(errors.isEmpty());
        assertEquals(4, errors.size());
        assertEquals("Veuillez renseigner le montant du paiement", errors.get(0));
        assertEquals("Veuillez renseigner la date du paiement", errors.get(1));
        assertEquals("Veuillez renseigner le mode de paiement", errors.get(2));
        assertEquals("Veuillez selectionner une facture pour le paiement", errors.get(3));
    }

    @Test
    @DisplayName("Paiement en especes complet : aucune erreur")
    void shouldAcceptValidEspecesPaiement() {
        assertTrue(PaiementValidator.validate(validPaiementEspeces().build()).isEmpty());
    }

    @Test
    @DisplayName("Montant absent : une seule erreur")
    void shouldRejectMissingMontant() {
        List<String> errors = PaiementValidator.validate(validPaiementEspeces().montant(null).build());

        assertEquals(1, errors.size());
        assertEquals("Veuillez renseigner le montant du paiement", errors.get(0));
    }

    @Test
    @DisplayName("Date absente : une seule erreur")
    void shouldRejectMissingDate() {
        List<String> errors = PaiementValidator.validate(validPaiementEspeces().datePaiement(null).build());

        assertEquals(1, errors.size());
        assertEquals("Veuillez renseigner la date du paiement", errors.get(0));
    }

    @Test
    @DisplayName("Mode de paiement absent : une seule erreur")
    void shouldRejectMissingMode() {
        List<String> errors = PaiementValidator.validate(validPaiementEspeces().modePaiement(null).build());

        assertEquals(1, errors.size());
        assertEquals("Veuillez renseigner le mode de paiement", errors.get(0));
    }

    @Test
    @DisplayName("Facture absente : une seule erreur")
    void shouldRejectMissingFacture() {
        List<String> errors = PaiementValidator.validate(validPaiementEspeces().factureId(null).build());

        assertEquals(1, errors.size());
        assertEquals("Veuillez selectionner une facture pour le paiement", errors.get(0));
    }

    @Test
    @DisplayName("Especes sans somme remise : une seule erreur")
    void shouldRejectEspecesWithoutMontantDonne() {
        List<String> errors = PaiementValidator.validate(validPaiementEspeces().montantDonne(null).build());

        assertEquals(1, errors.size());
        assertEquals("Veuillez renseigner la somme remise par le payeur pour un paiement en especes", errors.get(0));
    }

    @Test
    @DisplayName("Especes avec somme remise inferieure au montant : une seule erreur")
    void shouldRejectEspecesWithInsufficientMontantDonne() {
        List<String> errors = PaiementValidator.validate(
                validPaiementEspeces().montantDonne(new BigDecimal("50.00")).build());

        assertEquals(1, errors.size());
        assertEquals("La somme remise par le payeur est inferieure au montant du paiement", errors.get(0));
    }

    @Test
    @DisplayName("Especes avec somme remise superieure : aucune erreur")
    void shouldAcceptEspecesWithExcessMontantDonne() {
        assertTrue(PaiementValidator.validate(
                validPaiementEspeces().montantDonne(new BigDecimal("150.00")).build()).isEmpty());
    }

    @Test
    @DisplayName("Mobile money sans reference : une seule erreur")
    void shouldRejectMobileMoneyWithoutReference() {
        List<String> errors = PaiementValidator.validate(validPaiementEspeces()
                .modePaiement(Paiement.ModePaiement.MOBILE_MONEY)
                .montantDonne(null)
                .build());

        assertEquals(1, errors.size());
        assertEquals("Veuillez renseigner le code de reference du paiement", errors.get(0));
    }

    @Test
    @DisplayName("Cheque sans reference : une seule erreur")
    void shouldRejectChequeWithoutReference() {
        List<String> errors = PaiementValidator.validate(validPaiementEspeces()
                .modePaiement(Paiement.ModePaiement.CHEQUE)
                .build());

        assertEquals(1, errors.size());
        assertEquals("Veuillez renseigner le code de reference du paiement", errors.get(0));
    }

    @Test
    @DisplayName("Virement bancaire sans reference : une seule erreur")
    void shouldRejectVirementWithoutReference() {
        List<String> errors = PaiementValidator.validate(validPaiementEspeces()
                .modePaiement(Paiement.ModePaiement.VIREMENT_BANCAIRE)
                .build());

        assertEquals(1, errors.size());
        assertEquals("Veuillez renseigner le code de reference du paiement", errors.get(0));
    }

    @Test
    @DisplayName("Carte bancaire sans reference : une seule erreur")
    void shouldRejectCarteBancaireWithoutReference() {
        List<String> errors = PaiementValidator.validate(validPaiementEspeces()
                .modePaiement(Paiement.ModePaiement.CARTE_BANCAIRE)
                .build());

        assertEquals(1, errors.size());
        assertEquals("Veuillez renseigner le code de reference du paiement", errors.get(0));
    }

    @Test
    @DisplayName("Mobile money avec reference : aucune erreur")
    void shouldAcceptMobileMoneyWithReference() {
        assertTrue(PaiementValidator.validate(validPaiementEspeces()
                .modePaiement(Paiement.ModePaiement.MOBILE_MONEY)
                .montantDonne(null)
                .reference("MM-123")
                .build()).isEmpty());
    }

    @Test
    @DisplayName("Mode AUTRE sans reference : aucune erreur (branche default du switch)")
    void shouldAcceptAutreWithoutReference() {
        assertTrue(PaiementValidator.validate(validPaiementEspeces()
                .modePaiement(Paiement.ModePaiement.AUTRE)
                .montantDonne(null)
                .build()).isEmpty());
    }
}
