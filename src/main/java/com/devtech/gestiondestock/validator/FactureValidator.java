package com.devtech.gestiondestock.validator;

import com.devtech.gestiondestock.dto.FactureDto;
import com.devtech.gestiondestock.model.Facture;

import java.util.ArrayList;
import java.util.List;

public class FactureValidator {

    private FactureValidator() {
    }

    public static List<String> validate(FactureDto dto) {
        List<String> errors = new ArrayList<>();

        if (dto == null) {
            errors.add("Veuillez renseigner la date de la facture");
            errors.add("Veuillez renseigner le montant HT de la facture");
            errors.add("Veuillez renseigner le montant TTC de la facture");
            errors.add("Veuillez selectionner un client ou un fournisseur pour la facture");
            return errors;
        }

        if (dto.getDateFacture() == null) {
            errors.add("Veuillez renseigner la date de la facture");
        }
        if (dto.getMontantHT() == null) {
            errors.add("Veuillez renseigner le montant HT de la facture");
        }
        if (dto.getMontantTTC() == null) {
            errors.add("Veuillez renseigner le montant TTC de la facture");
        }

        // Le numero est genere par le serveur s'il n'est pas fourni : il n'est donc plus obligatoire.
        boolean fournisseur = Facture.TypeFacture.FOURNISSEUR.equals(dto.getTypeFacture());
        if (fournisseur) {
            if (dto.getFournisseurId() == null) {
                errors.add("Veuillez selectionner un fournisseur pour la facture");
            }
        } else if (dto.getClientId() == null) {
            errors.add("Veuillez selectionner un client pour la facture");
        }

        // Lien polymorphe : le type et l'identifiant du document source vont ensemble.
        if (dto.getSourceType() != null && dto.getSourceId() == null) {
            errors.add("Veuillez renseigner l'identifiant du document source de la facture");
        }
        if (dto.getSourceId() != null && dto.getSourceType() == null) {
            errors.add("Veuillez renseigner le type du document source de la facture");
        }

        return errors;
    }
}
