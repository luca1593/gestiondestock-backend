package com.devtech.gestiondestock.validator;

import com.devtech.gestiondestock.dto.FactureDto;
import com.devtech.gestiondestock.model.Facture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests unitaires du validateur de facture.
 *
 * Champs obligatoires : dateFacture, montantHT, montantTTC, et selon le type
 * (CLIENT par defaut ou FOURNISSEUR) le clientId ou le fournisseurId.
 * Le lien polymorphe sourceType/sourceId doit etre renseigne par paire.
 * numeroFacture n'est plus obligatoire (genere par le serveur).
 */
class FactureValidatorTest {

    private FactureDto.FactureDtoBuilder validFactureClient() {
        return FactureDto.builder()
                .dateFacture(LocalDateTime.parse("2026-01-01T10:00:00"))
                .montantHT(new BigDecimal("100.00"))
                .montantTTC(new BigDecimal("118.00"))
                .typeFacture(Facture.TypeFacture.CLIENT)
                .clientId(1);
    }

    private FactureDto.FactureDtoBuilder validFactureFournisseur() {
        return FactureDto.builder()
                .dateFacture(LocalDateTime.parse("2026-01-01T10:00:00"))
                .montantHT(new BigDecimal("100.00"))
                .montantTTC(new BigDecimal("118.00"))
                .typeFacture(Facture.TypeFacture.FOURNISSEUR)
                .fournisseurId(1);
    }

    @Test
    @DisplayName("Facture null : les 4 messages obligatoires")
    void shouldRejectNullFacture() {
        List<String> errors = FactureValidator.validate(null);

        assertFalse(errors.isEmpty());
        assertEquals(4, errors.size());
        assertEquals("Veuillez renseigner la date de la facture", errors.get(0));
        assertEquals("Veuillez renseigner le montant HT de la facture", errors.get(1));
        assertEquals("Veuillez renseigner le montant TTC de la facture", errors.get(2));
        assertEquals("Veuillez selectionner un client ou un fournisseur pour la facture", errors.get(3));
    }

    @Test
    @DisplayName("Facture client complete : aucune erreur")
    void shouldAcceptValidClientFacture() {
        assertTrue(FactureValidator.validate(validFactureClient().build()).isEmpty());
    }

    @Test
    @DisplayName("Facture fournisseur complete : aucune erreur")
    void shouldAcceptValidFournisseurFacture() {
        assertTrue(FactureValidator.validate(validFactureFournisseur().build()).isEmpty());
    }

    @Test
    @DisplayName("Date absente : une seule erreur")
    void shouldRejectMissingDate() {
        List<String> errors = FactureValidator.validate(validFactureClient().dateFacture(null).build());

        assertEquals(1, errors.size());
        assertEquals("Veuillez renseigner la date de la facture", errors.get(0));
    }

    @Test
    @DisplayName("Montant HT absent : une seule erreur")
    void shouldRejectMissingMontantHT() {
        List<String> errors = FactureValidator.validate(validFactureClient().montantHT(null).build());

        assertEquals(1, errors.size());
        assertEquals("Veuillez renseigner le montant HT de la facture", errors.get(0));
    }

    @Test
    @DisplayName("Montant TTC absent : une seule erreur")
    void shouldRejectMissingMontantTTC() {
        List<String> errors = FactureValidator.validate(validFactureClient().montantTTC(null).build());

        assertEquals(1, errors.size());
        assertEquals("Veuillez renseigner le montant TTC de la facture", errors.get(0));
    }

    @Test
    @DisplayName("Type non FOURNISSEUR et clientId absent : erreur sur le client")
    void shouldRejectMissingClient() {
        List<String> errors = FactureValidator.validate(validFactureClient().clientId(null).build());

        assertEquals(1, errors.size());
        assertEquals("Veuillez selectionner un client pour la facture", errors.get(0));
    }

    @Test
    @DisplayName("Type null : traite comme CLIENT, clientId requis")
    void shouldDefaultToClientWhenTypeNull() {
        List<String> errors = FactureValidator.validate(validFactureClient().typeFacture(null).clientId(null).build());

        assertEquals(1, errors.size());
        assertEquals("Veuillez selectionner un client pour la facture", errors.get(0));
    }

    @Test
    @DisplayName("Type FOURNISSEUR et fournisseurId absent : erreur sur le fournisseur")
    void shouldRejectMissingFournisseur() {
        List<String> errors = FactureValidator.validate(validFactureFournisseur().fournisseurId(null).build());

        assertEquals(1, errors.size());
        assertEquals("Veuillez selectionner un fournisseur pour la facture", errors.get(0));
    }

    @Test
    @DisplayName("Type FOURNISSEUR : clientId non requis")
    void shouldNotRequireClientForFournisseurFacture() {
        assertTrue(FactureValidator.validate(validFactureFournisseur().clientId(null).build()).isEmpty());
    }

    @Test
    @DisplayName("SourceType sans sourceId : erreur sur l'identifiant source")
    void shouldRejectSourceTypeWithoutSourceId() {
        List<String> errors = FactureValidator.validate(
                validFactureClient().sourceType(Facture.SourceDocument.VENTE).build());

        assertEquals(1, errors.size());
        assertEquals("Veuillez renseigner l'identifiant du document source de la facture", errors.get(0));
    }

    @Test
    @DisplayName("SourceId sans sourceType : erreur sur le type source")
    void shouldRejectSourceIdWithoutSourceType() {
        List<String> errors = FactureValidator.validate(validFactureClient().sourceId(42).build());

        assertEquals(1, errors.size());
        assertEquals("Veuillez renseigner le type du document source de la facture", errors.get(0));
    }

    @Test
    @DisplayName("Numero de facture absent : aucune erreur (genere par le serveur)")
    void shouldAcceptMissingNumeroFacture() {
        assertTrue(FactureValidator.validate(validFactureClient().numeroFacture(null).build()).isEmpty());
    }
}
