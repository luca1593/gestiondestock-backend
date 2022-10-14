package com.devtech.gestiondestock.validator;

import com.devtech.gestiondestock.dto.CategoryDto;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class CategoryValidator {
    public static List<String> validate(CategoryDto categoryDto){
        List<String> errors = new ArrayList<>();

        if (categoryDto == null){
            errors.add("Veuillez renseigner le code de la categorie");
            return errors;
        }

        if ( categoryDto.getCode() == null || !StringUtils.hasLength(categoryDto.getCode())){
            errors.add("Veuillez renseigner le code de la categorie");
        }

        return errors;
    }

}
