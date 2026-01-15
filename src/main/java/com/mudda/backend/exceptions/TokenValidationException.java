/**
 * ---------------------------------------------------------------
 * Project : Mudda
 * File    : VerificationTokenValidationException
 * Author  : Vikas Kumar
 * Created : 15-01-2026
 * ---------------------------------------------------------------
 */
package com.mudda.backend.exceptions;

import com.mudda.backend.token.TokenType;
import lombok.Getter;

@Getter
public class TokenValidationException extends RuntimeException {

    private final TokenType tokenType;
    private final TokenFailureReason failureReason;

    public TokenValidationException(TokenType tokenType1, TokenFailureReason failureReason) {
        this.tokenType = tokenType1;
        this.failureReason = failureReason;
    }

}
