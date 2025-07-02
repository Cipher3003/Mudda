package com.mudda.backend.utils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.mudda.backend.exceptions.DatabaseSaveException;
import com.mudda.backend.exceptions.FileConversionException;
import com.mudda.backend.exceptions.FileSizeLimitExceedException;
import com.mudda.backend.exceptions.InvalidImageExtensionException;

import lombok.extern.log4j.Log4j2;

@Log4j2
@RestControllerAdvice
public class GlobalExceptionHandler {

    private final MessageUtil messageUtil;

    public GlobalExceptionHandler(MessageUtil messageUtil) {
        this.messageUtil = messageUtil;
    }

    @ExceptionHandler(FileConversionException.class)
    public ResponseEntity<?> handleFileConversion(FileConversionException e) {
        // Logged in the service class
        String localizedMessage = messageUtil.getMessage(e.getErrorMessageCode(), e.getArgs());
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(localizedMessage);
    }

    @ExceptionHandler(InvalidImageExtensionException.class)
    public ResponseEntity<?> handleBadImage(InvalidImageExtensionException e) {
        String localizedMessage = messageUtil.getMessage(e.getErrorMessageCode(), e.getArgs());
        log.warn("InvalidImageExtensionException : {}", localizedMessage);
        return ResponseEntity
                .badRequest()
                .body(localizedMessage);
    }

    @ExceptionHandler(AmazonS3Exception.class)
    public ResponseEntity<?> handleAmazonS3Failure(AmazonS3Exception e) {
        log.warn("AmazonS3Exception: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(e.getMessage());
    }

    @ExceptionHandler(DatabaseSaveException.class)
    public ResponseEntity<?> handleDatabaseSaveFailure(DatabaseSaveException e) {
        String localizedMessage = messageUtil.getMessage(e.getErrorMessageCode(), e.getArgs());
        log.warn("DatabaseSaveException: {}", localizedMessage);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(localizedMessage);
    }

    @ExceptionHandler(FileSizeLimitExceedException.class)
    public ResponseEntity<?> handleTooLargeFile(FileSizeLimitExceedException e) {
        String localizedMessage = messageUtil.getMessage(e.getErrorMessageCode(), e.getArgs());
        log.warn("FileSizeLimitExceededException: {}", localizedMessage);
        return ResponseEntity
                .status(HttpStatus.PAYLOAD_TOO_LARGE)
                .body(localizedMessage);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<?> handleTooLargeFile(MaxUploadSizeExceededException e) {
        log.warn("MaxUploadSizeExceededException: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.PAYLOAD_TOO_LARGE)
                .body(e.getMessage());
    }

}
