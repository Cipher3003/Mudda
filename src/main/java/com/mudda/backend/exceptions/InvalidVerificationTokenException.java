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

public class InvalidVerificationTokenException extends LocalizedException {

    public InvalidVerificationTokenException(Object... args) {
        super(MessageCodes.INVALID_REFRESH_TOKEN, args);
    }
}
