/**
 * ---------------------------------------------------------------
 * Project : Mudda
 * File    : VerificationTokenExpiredException
 * Author  : Vikas Kumar
 * Created : 11-04-2026
 * ---------------------------------------------------------------
 */
package com.mudda.backend.exceptions;

import com.mudda.backend.utils.MessageCodes;
import org.springframework.http.HttpStatus;

public class VerificationTokenExpiredException extends ApiException {
    public VerificationTokenExpiredException() {
        super(HttpStatus.GONE, MessageCodes.TOKEN_EXPIRED);
    }
}
