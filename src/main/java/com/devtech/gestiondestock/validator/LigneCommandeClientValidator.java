package com.devtech.gestiondestock.validator;

import com.devtech.gestiondestock.dto.LigneCommandeClientDto;

import java.util.ArrayList;
import java.util.List;

public class LigneCommandeClientValidator {

    public static List<String> validate(LigneCommandeClientDto ligneCommandeClientDto) {
        List<String> errors = new ArrayList<>();

        if (ligneCommandeClientDto == null){
            errors.add("Veuillez renseigner la quantite de la commande");
            return errors;
        }

        if (ligneCommandeClientDto.getQuantite() == null) {
            errors.add("Veuillez renseigner la quantite de la commande");
        }

        return errors;
    }

}
