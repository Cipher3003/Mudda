package com.mudda.backend.exceptions;

import lombok.Getter;

/**
 * ---------------------------------------------------------------
 * Project : Mudda
 * File    : DuplicateEntityException
 * Author  : Vikas Kumar
 * Created : 13-11-2025
 * ---------------------------------------------------------------
 */
@Getter
public class DuplicateEntityException extends RuntimeException {
    private final String errorMessageCode;
    private final Object[] args;

    public DuplicateEntityException(String errorMessageCode, Object... args) {
        super(errorMessageCode);
        this.errorMessageCode = errorMessageCode;
        this.args = args;
    }
}
