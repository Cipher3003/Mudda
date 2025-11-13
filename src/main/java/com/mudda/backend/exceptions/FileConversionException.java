package com.mudda.backend.exceptions;

import lombok.Getter;

@Getter
public class FileConversionException extends RuntimeException {

    private final String errorMessageCode;
    private final Object[] args;

    public FileConversionException(String errorMessageCode, Object... args) {
        super(errorMessageCode);
        this.errorMessageCode = errorMessageCode;
        this.args = args;
    }

}
