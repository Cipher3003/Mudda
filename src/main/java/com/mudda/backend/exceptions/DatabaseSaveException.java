package com.mudda.backend.exceptions;

import com.mudda.backend.utils.MessageCodes;
import com.mudda.backend.utils.MessageUtil;

public class DatabaseSaveException extends RuntimeException {
    public DatabaseSaveException() {
        super(MessageUtil.getMessage(MessageCodes.DATABASE_SAVE_ERROR));
    }

    public DatabaseSaveException(Throwable cause) {
        super(MessageUtil.getMessage(MessageCodes.DATABASE_SAVE_ERROR), cause);
    }
}
