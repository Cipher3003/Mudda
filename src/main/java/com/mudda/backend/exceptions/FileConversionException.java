package com.mudda.backend.exceptions;

import com.mudda.backend.utils.MessageCodes;
import com.mudda.backend.utils.MessageUtil;

public class FileConversionException extends RuntimeException {
    public FileConversionException() {
        super(MessageUtil.getMessage(MessageCodes.MULTIPART_TO_FILE_CONVERT_EXCEPT));
    }
}
