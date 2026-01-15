/**
 * ---------------------------------------------------------------
 * Project : Mudda
 * File    : UsernameAlreadyExistsException
 * Author  : Vikas Kumar
 * Created : 15-01-2026
 * ---------------------------------------------------------------
 */
package com.mudda.backend.exceptions;

import com.mudda.backend.utils.MessageCodes;

public class UsernameAlreadyExistsException extends LocalizedException {
    public UsernameAlreadyExistsException() {
        super(MessageCodes.USERNAME_ALREADY_EXISTS);
    }
}
