package com.devtech.gestiondestock.validator;

import com.devtech.gestiondestock.dto.FactureDto;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class FactureValidator {

    private FactureValidator() {
    }

    public static List<String> validate(FactureDto dto) {
        List<String> errors = new ArrayList<>();

        if (dto == null) {
            errors.add("Veuillez renseigner le numéro de la facture");
            errors.add("Veuillez renseigner la date de la facture");
            errors.add("Veuillez renseigner le montant HT de la facture");
            errors.add("Veuillez renseigner le montant TTC de la facture");
            errors.add("Veuillez selectionner un client pour la facture");
            return errors;
        }

        if (!StringUtils.hasLength(dto.getNumeroFacture())) {
            errors.add("Veuillez renseigner le numéro de la facture");
        }
        if (dto.getDateFacture() == null) {
            errors.add("Veuillez renseigner la date de la facture");
        }
        if (dto.getMontantHT() == null) {
            errors.add("Veuillez renseigner le montant HT de la facture");
        }
        if (dto.getMontantTTC() == null) {
            errors.add("Veuillez renseigner le montant TTC de la facture");
        }
        if (dto.getClientId() == null) {
            errors.add("Veuillez selectionner un client pour la facture");
        }

        return errors;
    }
}
