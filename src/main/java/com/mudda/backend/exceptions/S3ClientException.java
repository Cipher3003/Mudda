package com.mudda.backend.exceptions;

import com.mudda.backend.utils.MessageCodes;

public class S3ClientException extends LocalizedException {

    public S3ClientException() {
        super(MessageCodes.STORAGE_CLIENT_ERROR);
    }

}
