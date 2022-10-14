package com.devtech.gestiondestock.validator;

import com.devtech.gestiondestock.dto.LigneCommandeFournisseurDto;

import java.util.ArrayList;
import java.util.List;

public class LigneCommandeFournisseurValidator {

    public static List<String> validate(LigneCommandeFournisseurDto ligneCommandeFournisseurDto) {
        List<String> errors = new ArrayList<>();

        if (ligneCommandeFournisseurDto == null){
            errors.add("Veuillez renseigner la quantite de la commande");
            return errors;
        }

        if (ligneCommandeFournisseurDto.getQuantite() == null) {
            errors.add("Veuillez renseigner la quantite de la commande");
        }

        return errors;
    }

}
