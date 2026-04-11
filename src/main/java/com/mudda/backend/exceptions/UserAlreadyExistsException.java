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
import org.springframework.http.HttpStatus;

public class UserAlreadyExistsException extends ApiException {
    public UserAlreadyExistsException() {
        super(HttpStatus.CONFLICT, MessageCodes.USER_ALREADY_EXISTS);
    }
}
