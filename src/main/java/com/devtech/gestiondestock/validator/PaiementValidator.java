package com.devtech.gestiondestock.validator;

import com.devtech.gestiondestock.dto.PaiementDto;
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

        return errors;
    }
}
