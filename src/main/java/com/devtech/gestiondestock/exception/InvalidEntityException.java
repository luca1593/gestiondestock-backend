package com.devtech.gestiondestock.exception;

import lombok.Getter;

import java.util.List;

public class InvalidEntityException extends RuntimeException {

    @Getter
    private ErrorsCode errorsCode;
    @Getter
    private List<String> errors;

    public InvalidEntityException(String message) {
        super(message);
    }

    public InvalidEntityException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidEntityException(String message, Throwable cause, ErrorsCode errorsCode) {
        super(message, cause);
        this.errorsCode = errorsCode;
    }

    public InvalidEntityException(String message, ErrorsCode errorsCode) {
        super(message);
        this.errorsCode = errorsCode;
    }

    public InvalidEntityException(String message, ErrorsCode errorsCode, List<String> errors){
        super(message);
        this.errorsCode = errorsCode;
        this.errors = errors;
    }

}
