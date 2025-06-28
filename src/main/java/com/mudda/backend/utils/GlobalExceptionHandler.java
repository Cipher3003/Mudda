package com.mudda.backend.utils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.mudda.backend.exceptions.FileConversionException;
import com.mudda.backend.exceptions.InvalidImageExtensionException;

import lombok.extern.log4j.Log4j2;

@Log4j2
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(FileConversionException.class)
    public ResponseEntity<?> handleFileConversion(FileConversionException e) {
        // Logged in the service class
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(e.getMessage());
    }

    @ExceptionHandler(InvalidImageExtensionException.class)
    public ResponseEntity<?> handleBadImage(InvalidImageExtensionException e) {
        log.warn("InvalidImageExtensionException : {}", e.getMessage());
        return ResponseEntity
                .badRequest()
                .body(e.getMessage());
    }

    @ExceptionHandler(AmazonS3Exception.class)
    public ResponseEntity<?> handleAmazonS3Failure(AmazonS3Exception e) {
        log.warn("AmazonS3Exception: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(e.getMessage());
    }

}
