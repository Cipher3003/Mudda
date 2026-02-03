package com.mudda.backend.exceptions;

import com.mudda.backend.utils.MessageCodes;

public class UploadFailedException extends LocalizedException {

    public UploadFailedException() {
        super(MessageCodes.UPLOAD_FAILED);
    }

}
