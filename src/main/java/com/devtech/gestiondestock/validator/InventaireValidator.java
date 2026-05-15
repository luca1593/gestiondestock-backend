package com.devtech.gestiondestock.validator;

import com.devtech.gestiondestock.dto.InventaireDto;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class InventaireValidator {

    private InventaireValidator() {
    }

    public static List<String> validate(InventaireDto dto) {
        List<String> errors = new ArrayList<>();

        if (dto == null) {
            errors.add("Veuillez renseigner le code de l'inventaire");
            errors.add("Veuillez renseigner la date de début de l'inventaire");
            errors.add("Veuillez selectionner un entrepôt pour l'inventaire");
            return errors;
        }

        if (!StringUtils.hasLength(dto.getCode())) {
            errors.add("Veuillez renseigner le code de l'inventaire");
        }
        if (dto.getDateDebut() == null) {
            errors.add("Veuillez renseigner la date de début de l'inventaire");
        }
        if (dto.getEntrepotId() == null) {
            errors.add("Veuillez selectionner un entrepôt pour l'inventaire");
        }

        return errors;
    }
}
