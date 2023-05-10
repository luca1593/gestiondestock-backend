package com.devtech.gestiondestock.validator;

import com.devtech.gestiondestock.dto.CommandeFournisseurDto;

import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class CommandeFournisseurValidator {
    public static List<String> validate(CommandeFournisseurDto commandeFournisseurDto) {
        List<String> errors = new ArrayList<>();

        if (commandeFournisseurDto == null){
            errors.add("Veuillez renseigner le code de la commande");
            errors.add("Veuillez renseigner la date de la commande");
            errors.add("Veuillez renseigner l'etat de la commande");
            errors.add("Veuillez renseigner le fournisseur pour la commande");
            errors.add("Veuillez ajouter au moins un article pour la commande");
            return errors;
        }

        if (!StringUtils.hasLength(commandeFournisseurDto.getCode())) {
            errors.add("Veuillez renseigner le code de la commande");
        }
        if (commandeFournisseurDto.getDateCommande() == null) {
            errors.add("Veuillez renseigner la date de la commande");
        }
        if (!StringUtils.hasLength(commandeFournisseurDto.getEtatcommande().toString())) {
            errors.add("Veuillez renseigner l'etat de la commande");
        }
        if (commandeFournisseurDto.getFournisseur() == null || commandeFournisseurDto.getFournisseur().getId() == null){
            errors.add("Veuillez renseigner le fournisseur pour la commande");
        }

        if (CollectionUtils.isEmpty(commandeFournisseurDto.getLigneCommandeFournisseurs())) {
            errors.add("Veuillez ajouter au moins un article pour la commande");
        }

        return errors;
    }
}
