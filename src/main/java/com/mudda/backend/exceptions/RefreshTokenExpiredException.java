/**
 * ---------------------------------------------------------------
 * Project : Mudda
 * File    : RefreshTokenExpiredException
 * Author  : Vikas Kumar
 * Created : 11-04-2026
 * ---------------------------------------------------------------
 */
package com.mudda.backend.exceptions;

import com.mudda.backend.utils.MessageCodes;
import org.springframework.http.HttpStatus;

public class RefreshTokenExpiredException extends ApiException {
    public RefreshTokenExpiredException() {
        super(HttpStatus.UNAUTHORIZED, MessageCodes.AUTHENTICATION_REQUIRED);
    }
}
