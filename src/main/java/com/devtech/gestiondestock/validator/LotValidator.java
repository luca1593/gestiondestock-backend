package com.devtech.gestiondestock.validator;

import com.devtech.gestiondestock.dto.LotDto;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class LotValidator {

    private LotValidator() {
    }

    public static List<String> validate(LotDto dto) {
        List<String> errors = new ArrayList<>();

        if (dto == null) {
            errors.add("Veuillez renseigner le numéro du lot");
            errors.add("Veuillez renseigner la quantité du lot");
            errors.add("Veuillez renseigner le prix d'achat du lot");
            errors.add("Veuillez selectionner un article pour le lot");
            errors.add("Veuillez selectionner un entrepôt pour le lot");
            return errors;
        }

        if (!StringUtils.hasLength(dto.getNumeroLot())) {
            errors.add("Veuillez renseigner le numéro du lot");
        }
        if (dto.getQuantite() == null) {
            errors.add("Veuillez renseigner la quantité du lot");
        }
        if (dto.getPrixAchat() == null) {
            errors.add("Veuillez renseigner le prix d'achat du lot");
        }
        if (dto.getArticleId() == null) {
            errors.add("Veuillez selectionner un article pour le lot");
        }
        if (dto.getEntrepotId() == null) {
            errors.add("Veuillez selectionner un entrepôt pour le lot");
        }

        return errors;
    }
}
