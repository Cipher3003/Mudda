package com.mudda.backend.exceptions;

public class DatabaseSaveException extends RuntimeException {

    private final String errorMessageCode;
    private final Object[] args;

    public DatabaseSaveException(String errorMessageCode, Object... args) {
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
