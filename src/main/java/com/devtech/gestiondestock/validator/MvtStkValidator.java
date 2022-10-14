package com.devtech.gestiondestock.validator;

import com.devtech.gestiondestock.dto.MvtStkDto;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class MvtStkValidator {

    public static List<String> validate(MvtStkDto mvtStkDto) {
        List<String> errors = new ArrayList<>();

        if (mvtStkDto == null){
            errors.add("Veuillez renseigner la date du mouvement");
            errors.add("Veuillez renseigner la quantite du mouvement");
            errors.add("Veuillez renseigner la type du mouvement");
            errors.add("Veuillez renseigner l'article du mouvement");
            return errors;
        }

        if (mvtStkDto.getDateMvt() == null) {
            errors.add("Veuillez renseigner la date du mouvement");
        }
        if (mvtStkDto.getQuantite() == null || mvtStkDto.getQuantite().compareTo(BigDecimal.ZERO) == 0) {
            errors.add("Veuillez renseigner la quantite du mouvement");
        }
        if (mvtStkDto.getArticle() == null || mvtStkDto.getArticle().getId() == null) {
            errors.add("Veuillez renseigner l'article du mouvement");
        }
        if (!StringUtils.hasLength(mvtStkDto.getTypeMvt().name())) {
            errors.add("Veuillez renseigner la type du mouvement");
        }

        return errors;
    }

}
