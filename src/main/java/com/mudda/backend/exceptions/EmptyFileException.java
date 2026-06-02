package com.mudda.backend.exceptions;

import com.mudda.backend.utils.MessageCodes;
import org.springframework.http.HttpStatus;

public class EmptyFileException extends ApiException {

    public EmptyFileException(Object... args) {
        super(HttpStatus.BAD_REQUEST, MessageCodes.EMPTY_FILE, args);
    }

}
