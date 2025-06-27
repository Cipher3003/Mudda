package com.mudda.backend.exceptions;

import java.util.List;

import com.mudda.backend.utils.MessageCodes;
import com.mudda.backend.utils.MessageUtil;

public class InvalidImageExtensionException extends RuntimeException {
    public InvalidImageExtensionException(List<String> validExtensions) {
        super(MessageUtil.getMessage(
                MessageCodes.INVALID_IMAGE_EXTENSION,
                String.join(", ", validExtensions)));
    }
}
