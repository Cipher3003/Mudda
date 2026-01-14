package com.mudda.backend.exceptions;

import com.mudda.backend.utils.MessageCodes;

public class NonImageFileException extends LocalizedException {

    public NonImageFileException() {
        super(MessageCodes.FILE_NOT_IMAGE);
    }

}
