package com.mudda.backend.exceptions;

import com.mudda.backend.utils.MessageCodes;
import com.mudda.backend.utils.MessageUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.HashMap;
import java.util.Map;

@Log4j2
@RestControllerAdvice
public class GlobalExceptionHandler {

    private final MessageUtil messageUtil;

//    TODO: LocalizedException is currently treated as always 400
//    depends on where it’s thrown, not what it represents.

    public GlobalExceptionHandler(MessageUtil messageUtil) {
        this.messageUtil = messageUtil;
    }

    //    400 - validation & bad input
    @ExceptionHandler(value = {
            IllegalArgumentException.class,
            InvalidImageExtensionException.class
    })
    public ResponseEntity<ApiError> handleBadRequest(Exception e) {
        String message = resolveMessage(e, MessageCodes.BAD_REQUEST);
        return ResponseEntity.badRequest()
                .body(ApiError.of(HttpStatus.BAD_REQUEST, message));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException e) {

        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getFieldErrors()
                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

        return ResponseEntity.badRequest()
                .body(ApiError.validation(errors));
    }

    //    409 - conflicts
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleConflict(Exception e) {
        String message = resolveMessage(e, MessageCodes.CONFLICT);
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ApiError.of(HttpStatus.CONFLICT, message));
    }

    //    413 - payload too large
    @ExceptionHandler(value = {
            FileSizeLimitExceededException.class,
            MaxUploadSizeExceededException.class,
    })
    public ResponseEntity<ApiError> handlePayloadTooLarge(Exception e) {
        String message = resolveMessage(e, MessageCodes.PAYLOAD_TOO_LARGE);
        log.warn("{}: {}", e.getClass().getName(), message);
        return ResponseEntity
                .status(HttpStatus.PAYLOAD_TOO_LARGE)
                .body(ApiError.of(HttpStatus.PAYLOAD_TOO_LARGE, message));
    }

    //    401 - unauthorized
    @ExceptionHandler(value = {
            AuthenticationException.class,
            InvalidRefreshTokenException.class
    })
    public ResponseEntity<ApiError> handleAuthenticationException(Exception e) {
        String message = resolveMessage(e, MessageCodes.AUTHENTICATION_REQUIRED);
        log.warn("{} : {}", e.getClass().getSimpleName(), message);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiError.of(HttpStatus.UNAUTHORIZED, message));
    }

    //    503 - unavailable services
    @ExceptionHandler(value = {
            S3ClientException.class,
            S3ServiceException.class
    })
    public ResponseEntity<ApiError> handleStorage(Exception e) {
        String message = resolveMessage(e, MessageCodes.STORAGE_UNAVAILABLE);
        log.warn("S3ClientException: {}", message);
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(ApiError.of(HttpStatus.SERVICE_UNAVAILABLE, message));
    }

    //    500 - unexpected error
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleUnexpected(Exception e) {
        String message = resolveMessage(e, MessageCodes.UNEXPECTED_ERROR);
        log.warn("{}: {}", e.getClass().getName(), message);
        return ResponseEntity
                .internalServerError()
                .body(ApiError.of(HttpStatus.INTERNAL_SERVER_ERROR, message));
    }

    private String resolveMessage(Exception e, String fallbackMessage) {
        if (e instanceof LocalizedException le)
            return messageUtil.getMessage(le.getErrorMessageCode(), le.getArgs());

        return messageUtil.getMessage(fallbackMessage);
    }

}
