package com.devtech.gestiondestock.validator;

import com.devtech.gestiondestock.dto.PaiementDto;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class PaiementValidator {

    private PaiementValidator() {
    }

    public static List<String> validate(PaiementDto dto) {
        List<String> errors = new ArrayList<>();

        if (dto == null) {
            errors.add("Veuillez renseigner le montant du paiement");
            errors.add("Veuillez renseigner la date du paiement");
            errors.add("Veuillez renseigner le mode de paiement");
            errors.add("Veuillez selectionner une facture pour le paiement");
            return errors;
        }

        if (dto.getMontant() == null) {
            errors.add("Veuillez renseigner le montant du paiement");
        }
        if (dto.getDatePaiement() == null) {
            errors.add("Veuillez renseigner la date du paiement");
        }
        if (dto.getModePaiement() == null) {
            errors.add("Veuillez renseigner le mode de paiement");
        }
        if (dto.getFactureId() == null) {
            errors.add("Veuillez selectionner une facture pour le paiement");
        }

        if (dto.getModePaiement() != null) {
            switch (dto.getModePaiement()) {
                case ESPECES -> {
                    if (dto.getMontantDonne() == null) {
                        errors.add("Veuillez renseigner la somme remise par le payeur pour un paiement en especes");
                    } else if (dto.getMontant() != null && dto.getMontantDonne().compareTo(dto.getMontant()) < 0) {
                        errors.add("La somme remise par le payeur est inferieure au montant du paiement");
                    }
                }
                case MOBILE_MONEY, CARTE_BANCAIRE, VIREMENT_BANCAIRE, CHEQUE -> {
                    if (!StringUtils.hasLength(dto.getReference())) {
                        errors.add("Veuillez renseigner le code de reference du paiement");
                    }
                }
                default -> {
                    // ESPECES est le seul mode sans reference obligatoire.
                }
            }
        }

        return errors;
    }
}
