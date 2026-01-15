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

public class PhoneNumberAlreadyExistsException extends LocalizedException {
    public PhoneNumberAlreadyExistsException() {
        super(MessageCodes.PHONE_ALREADY_EXISTS);
    }
}
