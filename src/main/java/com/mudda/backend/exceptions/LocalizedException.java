/**
 * ---------------------------------------------------------------
 * Project : Mudda
 * File    : LocalizedException
 * Author  : Vikas Kumar
 * Created : 14-01-2026
 * ---------------------------------------------------------------
 */
package com.mudda.backend.exceptions;

public abstract class LocalizedException extends RuntimeException {

    private final String errorMessageCode;
    private final Object[] args;

    protected LocalizedException(String errorMessageCode, Object... args) {
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
