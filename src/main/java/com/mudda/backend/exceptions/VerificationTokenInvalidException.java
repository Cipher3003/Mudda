/**
 * ---------------------------------------------------------------
 * Project : Mudda
 * File    : InvalidTokenException
 * Author  : Vikas Kumar
 * Created : 14-01-2026
 * ---------------------------------------------------------------
 */
package com.mudda.backend.exceptions;

import com.mudda.backend.utils.MessageCodes;
import org.springframework.http.HttpStatus;

public class VerificationTokenInvalidException extends ApiException {

    public VerificationTokenInvalidException() {
        super(HttpStatus.BAD_REQUEST, MessageCodes.INVALID_VERIFICATION_TOKEN);
    }
}
