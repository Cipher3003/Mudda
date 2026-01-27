/**
 * ---------------------------------------------------------------
 * Project : Mudda
 * File    : LocalizedException
 * Author  : Vikas Kumar
 * Created : 14-01-2026
 * ---------------------------------------------------------------
 */
package com.mudda.backend.exceptions;

import lombok.Getter;

@Getter
public abstract class LocalizedException extends RuntimeException {

    private final String errorMessageCode;
    private final Object[] args;

    protected LocalizedException(String errorMessageCode, Object... args) {
        super(errorMessageCode);
        this.errorMessageCode = errorMessageCode;
        this.args = args;
    }

}
