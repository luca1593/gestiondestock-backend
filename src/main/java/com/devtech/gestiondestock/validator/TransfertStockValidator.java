package com.devtech.gestiondestock.validator;

import com.devtech.gestiondestock.dto.TransfertStockDto;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class TransfertStockValidator {

    private TransfertStockValidator() {
    }

    public static List<String> validate(TransfertStockDto dto) {
        List<String> errors = new ArrayList<>();

        if (dto == null) {
            errors.add("Veuillez renseigner le code du transfert");
            errors.add("Veuillez renseigner la date du transfert");
            errors.add("Veuillez selectionner un entrepôt source");
            errors.add("Veuillez selectionner un entrepôt de destination");
            return errors;
        }

        if (!StringUtils.hasLength(dto.getCode())) {
            errors.add("Veuillez renseigner le code du transfert");
        }
        if (dto.getDateTransfert() == null) {
            errors.add("Veuillez renseigner la date du transfert");
        }
        if (dto.getEntrepotSourceId() == null) {
            errors.add("Veuillez selectionner un entrepôt source");
        }
        if (dto.getEntrepotDestinationId() == null) {
            errors.add("Veuillez selectionner un entrepôt de destination");
        }
        if (dto.getEntrepotSourceId() != null && dto.getEntrepotSourceId().equals(dto.getEntrepotDestinationId())) {
            errors.add("L'entrepôt source et l'entrepôt de destination doivent être différents");
        }

        return errors;
    }
}
