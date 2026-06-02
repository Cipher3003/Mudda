package com.mudda.backend.exceptions;

import com.mudda.backend.utils.MessageCodes;
import org.springframework.http.HttpStatus;


public class InvalidImageExtensionException extends ApiException {

    public InvalidImageExtensionException(String allowed) {
        super(HttpStatus.BAD_REQUEST, MessageCodes.INVALID_IMAGE_EXTENSION, allowed);
    }

}
