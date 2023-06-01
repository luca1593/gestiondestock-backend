package com.devtech.gestiondestock.validator;

import com.devtech.gestiondestock.dto.VenteDto;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class VenteValidator {

    public static List<String> validate(VenteDto venteDto) {
        List<String> errors = new ArrayList<>();

        if (venteDto == null){
            errors.add("Veuillez renseigner le code de la vente");
            errors.add("Veuillez renseigner la date de la vente");
            errors.add("Veuillez ajouter au moins un article pour la vente");
            return errors;
        }

        if (!StringUtils.hasLength(venteDto.getCode())) {
            errors.add("Veuillez renseigner le code de la vente");
        }

        if (venteDto.getDateVente() == null) {
            errors.add("Veuillez renseigner la date de la vente");
        }

        if(CollectionUtils.isEmpty(venteDto.getLigneVentes())){
            errors.add("Veuillez ajouter au moins un article pour la vente");
        }

        return errors;
    }

}
