package com.mudda.backend.utils;

import jakarta.persistence.EntityNotFoundException;

import jakarta.persistence.OptimisticLockException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import com.mudda.backend.exceptions.S3ClientException;
import com.mudda.backend.exceptions.S3ServiceException;
import com.amazonaws.services.appintegrations.model.DuplicateResourceException;
import com.mudda.backend.exceptions.DatabaseSaveException;
import com.mudda.backend.exceptions.FileConversionException;
import com.mudda.backend.exceptions.FileSizeLimitExceededException;
import com.mudda.backend.exceptions.InvalidImageExtensionException;

import lombok.extern.log4j.Log4j2;

@Log4j2
@RestControllerAdvice
public class GlobalExceptionHandler {

    private final MessageUtil messageUtil;

    public GlobalExceptionHandler(MessageUtil messageUtil) {
        this.messageUtil = messageUtil;
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<?> handleDuplicateEntity(DuplicateResourceException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<?> handleNullPointerException(NullPointerException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
    }

    @ExceptionHandler(OptimisticLockException.class)
    public ResponseEntity<?> handleDatabaseLockingFailureException(OptimisticLockException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
    }

    // TODO: integrate messageUtil with this function properly
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<?> handleEntityNotFound(EntityNotFoundException e) {
        // Logged in the service class
        return ResponseEntity.badRequest().body(e.getMessage());
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

    // TODO: remove if not used
    @ExceptionHandler(DatabaseSaveException.class)
    public ResponseEntity<?> handleDatabaseSaveFailure(DatabaseSaveException e) {
        String localizedMessage = messageUtil.getMessage(e.getErrorMessageCode(), e.getArgs());
        log.warn("DatabaseSaveException: {}", localizedMessage);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(localizedMessage);
    }

    @ExceptionHandler(FileSizeLimitExceededException.class)
    public ResponseEntity<?> handleTooLargeFile(FileSizeLimitExceededException e) {
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

    @ExceptionHandler(S3ClientException.class)
    public ResponseEntity<?> handleS3Client(S3ClientException e) {
        String localizedMessage = messageUtil.getMessage(e.getErrorMessageCode(), e.getArgs());
        log.warn("S3ClientException: {}", localizedMessage);
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(e.getMessage());
    }

    @ExceptionHandler(S3ServiceException.class)
    public ResponseEntity<?> handleS3Service(S3ServiceException e) {
        String localizedMessage = messageUtil.getMessage(e.getErrorMessageCode(), e.getArgs());
        log.warn("S3ServiceException: {}", localizedMessage);
        return ResponseEntity
                .status(HttpStatus.BAD_GATEWAY)
                .body(e.getMessage());
    }

}
