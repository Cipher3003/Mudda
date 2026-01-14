package com.mudda.backend.exceptions;

import com.mudda.backend.utils.MessageCodes;

public class EmptyFileException extends LocalizedException {

    public EmptyFileException() {
        super(MessageCodes.EMPTY_FILE);
    }

}
