package com.devtech.gestiondestock.validator;

import com.devtech.gestiondestock.dto.RoleDto;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class RoleValidator {

    public static List<String> validate(RoleDto roleDto) {
        List<String> errors = new ArrayList<>();

        if (roleDto == null){
            errors.add("Veuillez renseigner le role");
            return errors;
        }

        if (!StringUtils.hasLength(roleDto.getRoleNom())) {
            errors.add("Veuillez renseigner le role");
        }

        return errors;
    }

}
