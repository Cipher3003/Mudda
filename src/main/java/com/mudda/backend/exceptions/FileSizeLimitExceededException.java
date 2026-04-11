package com.mudda.backend.exceptions;

import com.mudda.backend.utils.MessageCodes;
import org.springframework.http.HttpStatus;

public class FileSizeLimitExceededException extends ApiException {
    public FileSizeLimitExceededException(int fileSizeLimitMb) {
        super(HttpStatus.PAYLOAD_TOO_LARGE, MessageCodes.FILE_SIZE_EXCEED_LIMIT, fileSizeLimitMb);
    }

}
