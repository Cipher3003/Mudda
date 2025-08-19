package com.mudda.backend.exceptions;

public class EmptyFileException extends RuntimeException {

    private final String errorMessageCode;
    private final Object[] args;

    public EmptyFileException(String errorMessageCode, Object... args) {
        super(errorMessageCode);
        this.errorMessageCode = errorMessageCode;
        this.args = args;
    }

    public String getErrorMessageCode() {
        return errorMessageCode;
    }

    public Object[] getArgs() {
        return args;
    }

}
