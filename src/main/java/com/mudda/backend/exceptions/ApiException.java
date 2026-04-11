/**
 * ---------------------------------------------------------------
 * Project : Mudda
 * File    : ApiException
 * Author  : Vikas Kumar
 * Created : 11-04-2026
 * ---------------------------------------------------------------
 */
package com.mudda.backend.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ApiException extends RuntimeException {
    private final HttpStatus httpStatus;
    private final String messageCode;
    private final Object[] args;

    public ApiException(HttpStatus httpStatus, String messageCode, Object... args) {
        this.httpStatus = httpStatus;
        this.messageCode = messageCode;
        this.args = args;
    }

}
