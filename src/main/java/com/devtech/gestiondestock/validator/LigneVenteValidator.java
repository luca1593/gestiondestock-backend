package com.devtech.gestiondestock.validator;

import com.devtech.gestiondestock.dto.LigneCommandeClientDto;
import com.devtech.gestiondestock.dto.LigneVenteDto;

import java.util.ArrayList;
import java.util.List;

public class LigneVenteValidator {

    public static List<String> validate(LigneVenteDto ligneVenteDto) {
        List<String> errors = new ArrayList<>();

        if (ligneVenteDto == null){
            errors.add("Veuillez renseigner la quantite de la commande");
            return errors;
        }

        if (ligneVenteDto.getQuantite() == null) {
            errors.add("Veuillez renseigner la quantite de la commande");
        }
        return errors;
    }
}
