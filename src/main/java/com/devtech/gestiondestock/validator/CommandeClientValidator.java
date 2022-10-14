package com.devtech.gestiondestock.validator;

import com.devtech.gestiondestock.dto.CommandeClientDto;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class CommandeClientValidator {
    public static List<String> validate(CommandeClientDto commandeClientDto) {
        List<String> errors = new ArrayList<>();
        if (commandeClientDto == null){
            errors.add("Veuillez renseigner le code de la commande");
            errors.add("Veuillez renseigner la date de la commande");
            errors.add("Veuillez renseigner l'etat de la commande");
            errors.add("Veuillez renseigner le client pour la commande");
            return errors;
        }
        if (!StringUtils.hasLength(commandeClientDto.getCode())) {
            errors.add("Veuillez renseigner le code de la commande");
        }
        if (commandeClientDto.getDateCommande() == null) {
            errors.add("Veuillez renseigner la date de la commande");
        }
        if (!StringUtils.hasLength(commandeClientDto.getEtatcommande().toString())) {
            errors.add("Veuillez renseigner l'etat de la commande");
        }
        if (commandeClientDto.getClient() == null || commandeClientDto.getClient().getId() == null){
            errors.add("Veuillez renseigner le client pour la commande");
        }
        return errors;
    }
}
