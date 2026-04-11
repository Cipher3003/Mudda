package com.mudda.backend.exceptions;

import com.mudda.backend.utils.MessageCodes;
import org.springframework.http.HttpStatus;

public class S3ServiceException extends ApiException {

    public S3ServiceException() {
        super(HttpStatus.SERVICE_UNAVAILABLE, MessageCodes.STORAGE_UNAVAILABLE);
    }

}
