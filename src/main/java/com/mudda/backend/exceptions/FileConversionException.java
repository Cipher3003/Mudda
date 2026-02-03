package com.mudda.backend.exceptions;

import com.mudda.backend.utils.MessageCodes;

public class FileConversionException extends LocalizedException {
//    TODO: name not align with usage

    public FileConversionException() {
        super(MessageCodes.FILE_CONVERSION_FAILED);
    }

}
