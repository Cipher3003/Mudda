package com.mudda.backend.exceptions;

import com.mudda.backend.utils.MessageCodes;
import org.springframework.http.HttpStatus;

public class S3ClientException extends ApiException {

    public S3ClientException() {
        super(HttpStatus.SERVICE_UNAVAILABLE, MessageCodes.STORAGE_CLIENT_ERROR);
    }

}
