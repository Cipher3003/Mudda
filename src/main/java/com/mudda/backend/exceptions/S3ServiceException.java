package com.mudda.backend.exceptions;

import com.mudda.backend.utils.MessageCodes;

public class S3ServiceException extends LocalizedException {

    public S3ServiceException() {
        super(MessageCodes.STORAGE_UNAVAILABLE);
    }

}
