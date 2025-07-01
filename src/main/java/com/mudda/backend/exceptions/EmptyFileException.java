package com.mudda.backend.exceptions;

import com.mudda.backend.utils.MessageCodes;
import com.mudda.backend.utils.MessageUtil;

public class EmptyFileException extends RuntimeException {
    public EmptyFileException() {
        super(MessageUtil.getMessage(MessageCodes.EMPTY_FILE));
    }
}
