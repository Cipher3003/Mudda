package com.mudda.backend.exceptions;

import com.mudda.backend.utils.MessageCodes;
import com.mudda.backend.utils.MessageUtil;

public class FileSizeLimitExceedException extends RuntimeException {
    public FileSizeLimitExceedException(long actual, long expected) {
        super(MessageUtil.getMessage(MessageCodes.FILE_SIZE_EXCEED_LIMIT, actual, expected));
    }
}
