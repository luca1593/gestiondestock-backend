package com.devtech.gestiondestock.validator;

import com.devtech.gestiondestock.dto.ArticleDto;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author luca
 */
public class ArticleValidator {

    private ArticleValidator() {
        // Not implemented
    }

    public static List<String> validate(ArticleDto articleDto) {
        List<String> errors = new ArrayList<>();

        if (articleDto == null) {
            errors.add("Veuillez renseigner le code de l'article");
            errors.add("Veuillez renseigner la designation de l'article");
            errors.add("Veuillez renseigner le prix unitaire de l'article");
            errors.add("Veuillez renseigner le code TVA de l'article");
            errors.add("Veuillez renseigner le prix unitaire TTC de l'article");
            errors.add("Veuillez selectioner une categorie pour l'article");
            return errors;
        }

        if (!StringUtils.hasLength(articleDto.getCodeArticle())){
            errors.add("Veuillez renseigner le code de l'article");
        }
        if (!StringUtils.hasLength(articleDto.getDesignation())){
            errors.add("Veuillez renseigner la designation de l'article");
        }
        if (articleDto.getPrixUnitaireht() == null){
            errors.add("Veuillez renseigner le prix unitaire de l'article");
        }
        if (articleDto.getTauxTva() == null){
            errors.add("Veuillez renseigner le code TVA de l'article");
        }
        if (articleDto.getPrixTtc() == null){
            errors.add("Veuillez renseigner le prix unitaire TTC de l'article");
        }
        if (articleDto.getCategory() == null){
            errors.add("Veuillez selectioner une categorie pour l'article");
        }

        return errors;
    }
}
