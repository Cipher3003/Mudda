package com.mudda.backend.exceptions;

import com.mudda.backend.utils.MessageCodes;
import org.springframework.http.HttpStatus;

public class NonImageFileException extends ApiException {

    public NonImageFileException() {
        super(HttpStatus.BAD_REQUEST, MessageCodes.FILE_NOT_IMAGE);
    }

}
