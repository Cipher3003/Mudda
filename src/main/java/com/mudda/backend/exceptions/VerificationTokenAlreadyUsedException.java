/**
 * ---------------------------------------------------------------
 * Project : Mudda
 * File    : VerificationTokenAlreadyUsedException
 * Author  : Vikas Kumar
 * Created : 11-04-2026
 * ---------------------------------------------------------------
 */
package com.mudda.backend.exceptions;

import com.mudda.backend.utils.MessageCodes;
import org.springframework.http.HttpStatus;

public class VerificationTokenAlreadyUsedException extends ApiException {
    public VerificationTokenAlreadyUsedException() {
        super(HttpStatus.CONFLICT, MessageCodes.TOKEN_USED);
    }
}
