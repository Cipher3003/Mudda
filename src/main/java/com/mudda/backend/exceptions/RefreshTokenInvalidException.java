/**
 * ---------------------------------------------------------------
 * Project : Mudda
 * File    : InvalidRefreshTokenException
 * Author  : Vikas Kumar
 * Created : 13-01-2026
 * ---------------------------------------------------------------
 */
package com.mudda.backend.exceptions;

import com.mudda.backend.utils.MessageCodes;
import org.springframework.http.HttpStatus;

public class RefreshTokenInvalidException extends ApiException {

    public RefreshTokenInvalidException() {
        super(HttpStatus.UNAUTHORIZED, MessageCodes.INVALID_REFRESH_TOKEN);
    }
}
