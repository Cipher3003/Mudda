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
import lombok.Getter;

@Getter
public class InvalidRefreshTokenException extends LocalizedException {

    public InvalidRefreshTokenException(Object... args) {
        super(MessageCodes.INVALID_REFRESH_TOKEN, args);
    }
}
