package com.devtech.gestiondestock.validator;

import com.devtech.gestiondestock.dto.AdresseDto;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author luca
 */
public class AdresseValidator {

    private AdresseValidator() {
        // Not implement
    }

    public static List<String> validate(AdresseDto adresseDto) {
        List<String> errors = new ArrayList<>();

        if (adresseDto == null) {
            errors.add("Veuillez renseigner un addresse valide");
            errors.add("L'adresse 1 est obligatoire");
            errors.add("La ville est obligatoire");
            errors.add("Le code postal est obligatoire");
            errors.add("Le pays est obligatoire");
            return errors;
        }
        if (!StringUtils.hasLength(adresseDto.getAdresse1())){
            errors.add("L'adresse 1 est obligatoire");
        }
        if (!StringUtils.hasLength(adresseDto.getVille())){
            errors.add("La ville est obligatoire");
        }
        if (!StringUtils.hasLength(adresseDto.getCodePostal())){
            errors.add("Le code postal est obligatoire");
        }
        if (!StringUtils.hasLength(adresseDto.getPays())){
            errors.add("Le pays est obligatoire");
        }

        return errors;
    }
}
