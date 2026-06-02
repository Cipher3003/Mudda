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
import org.springframework.http.HttpStatus;

public class UsernameAlreadyExistsException extends ApiException {
    public UsernameAlreadyExistsException() {
        super(HttpStatus.CONFLICT, MessageCodes.USERNAME_ALREADY_EXISTS);
    }
}
