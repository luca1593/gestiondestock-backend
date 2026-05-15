package com.devtech.gestiondestock.validator;

import com.devtech.gestiondestock.dto.RegleTarifaireDto;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class RegleTarifaireValidator {

    private RegleTarifaireValidator() {
    }

    public static List<String> validate(RegleTarifaireDto dto) {
        List<String> errors = new ArrayList<>();

        if (dto == null) {
            errors.add("Veuillez renseigner le code de la règle tarifaire");
            errors.add("Veuillez renseigner le nom de la règle tarifaire");
            errors.add("Veuillez renseigner le type de règle");
            errors.add("Veuillez renseigner la valeur de la règle");
            errors.add("Veuillez renseigner la date de début de la règle");
            return errors;
        }

        if (!StringUtils.hasLength(dto.getCode())) {
            errors.add("Veuillez renseigner le code de la règle tarifaire");
        }
        if (!StringUtils.hasLength(dto.getNom())) {
            errors.add("Veuillez renseigner le nom de la règle tarifaire");
        }
        if (dto.getTypeRegle() == null) {
            errors.add("Veuillez renseigner le type de règle");
        }
        if (dto.getValeur() == null) {
            errors.add("Veuillez renseigner la valeur de la règle");
        }
        if (dto.getDateDebut() == null) {
            errors.add("Veuillez renseigner la date de début de la règle");
        }

        return errors;
    }
}
