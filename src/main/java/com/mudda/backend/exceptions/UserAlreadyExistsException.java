/**
 * ---------------------------------------------------------------
 * Project : Mudda
 * File    : UserAlreadyExistsException
 * Author  : Vikas Kumar
 * Created : 15-01-2026
 * ---------------------------------------------------------------
 */
package com.mudda.backend.exceptions;

import com.mudda.backend.utils.MessageCodes;

public class UserAlreadyExistsException extends LocalizedException {
    public UserAlreadyExistsException() {
        super(MessageCodes.USER_ALREADY_EXISTS);
    }
}
