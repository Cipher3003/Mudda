/**
 * ---------------------------------------------------------------
 * Project : Mudda
 * File    : TemplateLoadException
 * Author  : Vikas Kumar
 * Created : 6/3/2026
 * ---------------------------------------------------------------
 */
package com.mudda.backend.exceptions;

import com.mudda.backend.utils.MessageCodes;
import org.springframework.http.HttpStatus;

public class TemplateLoadException extends ApiException {
    public TemplateLoadException() {
        super(HttpStatus.INTERNAL_SERVER_ERROR, MessageCodes.TEMPLATE_LOAD_FAILED);
    }
}
