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

public class PasswordUnchangedException extends LocalizedException {

    public PasswordUnchangedException() {
        super(MessageCodes.PASSWORD_SAME_AS_OLD);
    }
}
