/**
 * ---------------------------------------------------------------
 * Project : Mudda
 * File    : RefreshTokenReuseDetectedException
 * Author  : Vikas Kumar
 * Created : 11-04-2026
 * ---------------------------------------------------------------
 */
package com.mudda.backend.exceptions;

import com.mudda.backend.utils.MessageCodes;
import org.springframework.http.HttpStatus;

public class RefreshTokenReuseDetectedException extends ApiException {
    public RefreshTokenReuseDetectedException() {
        super(HttpStatus.UNAUTHORIZED, MessageCodes.INVALID_REFRESH_TOKEN);
    }
}
