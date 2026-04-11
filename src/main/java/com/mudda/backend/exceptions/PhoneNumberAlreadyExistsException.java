/**
 * ---------------------------------------------------------------
 * Project : Mudda
 * File    : PhoneNumberAlreadyExistsException
 * Author  : Vikas Kumar
 * Created : 15-01-2026
 * ---------------------------------------------------------------
 */
package com.mudda.backend.exceptions;

import com.mudda.backend.utils.MessageCodes;
import org.springframework.http.HttpStatus;

public class PhoneNumberAlreadyExistsException extends ApiException {
    public PhoneNumberAlreadyExistsException() {
        super(HttpStatus.CONFLICT, MessageCodes.PHONE_ALREADY_EXISTS);
    }
}
