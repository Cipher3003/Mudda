package com.mudda.backend.exceptions;

import com.mudda.backend.utils.MessageCodes;


public class InvalidImageExtensionException extends LocalizedException {

    public InvalidImageExtensionException(String allowed) {
        super(MessageCodes.INVALID_IMAGE_EXTENSION, allowed);
    }

}
