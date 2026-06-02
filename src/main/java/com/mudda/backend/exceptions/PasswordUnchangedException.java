/**
 * ---------------------------------------------------------------
 * Project : Mudda
 * File    : PasswordUnchangedException
 * Author  : Vikas Kumar
 * Created : 16-01-2026
 * ---------------------------------------------------------------
 */
package com.mudda.backend.exceptions;

import com.mudda.backend.utils.MessageCodes;
import org.springframework.http.HttpStatus;

public class PasswordUnchangedException extends ApiException {

    public PasswordUnchangedException() {
        super(HttpStatus.BAD_REQUEST, MessageCodes.PASSWORD_SAME_AS_OLD);
    }
}
