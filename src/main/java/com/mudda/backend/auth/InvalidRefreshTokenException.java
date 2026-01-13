/**
 * ---------------------------------------------------------------
 * Project : Mudda
 * File    : InvalidRefreshTokenException
 * Author  : Vikas Kumar
 * Created : 13-01-2026
 * ---------------------------------------------------------------
 */
package com.mudda.backend.auth;

import lombok.Getter;

@Getter
public class InvalidRefreshTokenException extends RuntimeException {

    private final String errorMessageCode;
    private final Object[] args;

    public InvalidRefreshTokenException(String errorMessageCode, Object... args) {
        super(errorMessageCode);
        this.errorMessageCode = errorMessageCode;
        this.args = args;
    }
}
