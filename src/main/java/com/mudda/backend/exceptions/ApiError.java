/**
 * ---------------------------------------------------------------
 * Project : Mudda
 * File    : ApiError
 * Author  : Vikas Kumar
 * Created : 14-01-2026
 * ---------------------------------------------------------------
 */
package com.mudda.backend.exceptions;

import org.springframework.http.HttpStatus;

import java.util.Map;

public record ApiError(
        int status,
        String error,
        String message,
        Map<String, String> errors
) {

    static ApiError of(HttpStatus status, String message) {
        return new ApiError(
                status.value(),
                status.getReasonPhrase(),
                message,
                null
        );
    }

    static ApiError validation(Map<String, String> errors) {
        return new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "Validation failed",
                errors
        );
    }
}
