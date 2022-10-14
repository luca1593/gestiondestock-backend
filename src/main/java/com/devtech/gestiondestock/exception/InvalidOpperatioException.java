package com.devtech.gestiondestock.exception;

import lombok.Getter;

public class InvalidOpperatioException extends RuntimeException{

    @Getter
    private  ErrorsCode errorsCode;

    public InvalidOpperatioException(String message) {
        super(message);
    }

    public InvalidOpperatioException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidOpperatioException(String message, Throwable cause, ErrorsCode errorsCode) {
        super(message, cause);
        this.errorsCode = errorsCode;
    }

    public InvalidOpperatioException(String message, ErrorsCode errorsCode) {
        super(message);
        this.errorsCode = errorsCode;
    }

}
