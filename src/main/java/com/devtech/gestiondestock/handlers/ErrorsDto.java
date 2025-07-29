package com.devtech.gestiondestock.handlers;

import com.devtech.gestiondestock.exception.ErrorsCode;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author luca
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ErrorsDto {
    private Integer httpCode;

    private ErrorsCode errorsCode;

    private String message;

    private List<String> errors = new ArrayList<>();

}
