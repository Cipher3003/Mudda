package com.mudda.backend.exceptions;

import com.mudda.backend.utils.MessageCodes;
import org.springframework.http.HttpStatus;

public class UploadFailedException extends ApiException {

    public UploadFailedException() {
        super(HttpStatus.BAD_GATEWAY, MessageCodes.UPLOAD_FAILED);
    }

}
