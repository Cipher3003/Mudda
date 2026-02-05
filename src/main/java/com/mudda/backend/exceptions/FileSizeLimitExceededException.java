package com.mudda.backend.exceptions;

import com.mudda.backend.utils.MessageCodes;

public class FileSizeLimitExceededException extends LocalizedException {

    public FileSizeLimitExceededException(int fileSizeLimitMb) {
        super(MessageCodes.FILE_SIZE_EXCEED_LIMIT, fileSizeLimitMb);
    }

}
