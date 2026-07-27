package com.mudda.backend.exceptions;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.mudda.backend.utils.MessageCodes;
import com.mudda.backend.utils.MessageUtil;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.UnexpectedTypeException;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.ClientAbortException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.async.AsyncRequestNotUsableException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private final MessageUtil messageUtil;

    public GlobalExceptionHandler(MessageUtil messageUtil) {
        this.messageUtil = messageUtil;
    }

    //region Specialized Handlers

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException e) {
        Map<String, String> errors = new LinkedHashMap<>();
        e.getBindingResult().getFieldErrors()
                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

        return ApiError.responseValidation(errors);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiError> handleArgumentTypeMismatch(MethodArgumentTypeMismatchException e) {
        String parameterName = e.getName();
        String message = messageUtil.getMessage(MessageCodes.VALIDATION_TYPE_MISMATCH, parameterName);
        return ApiError.responseBad(message);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleReadableException(HttpMessageNotReadableException e) {
        if (e.getCause() instanceof InvalidFormatException ife && ife.getTargetType().isEnum()) {
            String message = messageUtil.getMessage(MessageCodes.VALIDATION_INVALID_ENUM,
                    ife.getValue(), ife.getTargetType().getSimpleName());
            return ApiError.responseBad(message);
        }

        String message = messageUtil.getMessage(MessageCodes.VALIDATION_INVALID_JSON);
        return ApiError.responseBad(message);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiError> handleBadCredentials() {
        return apiErrorResponse(HttpStatus.UNAUTHORIZED, MessageCodes.INVALID_CREDENTIALS);
    }

    @ExceptionHandler(LockedException.class)
    public ResponseEntity<ApiError> handleAccountLocked() {
        return apiErrorResponse(HttpStatus.LOCKED, MessageCodes.ACCOUNT_LOCKED);
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ApiError> handleAccountDisabled() {
        return apiErrorResponse(HttpStatus.UNAUTHORIZED, MessageCodes.ACCOUNT_NOT_VERIFIED);
    }

    @ExceptionHandler(value = {
            UnexpectedTypeException.class,
            MissingServletRequestParameterException.class,
            MissingServletRequestPartException.class,
            HttpMediaTypeNotSupportedException.class,
            MultipartException.class
    })
    public ResponseEntity<ApiError> handleFrameworkBadRequest(Exception e) {
        return ApiError.responseBad(e.getMessage());
    }

    @ExceptionHandler(value = {AsyncRequestNotUsableException.class, ClientAbortException.class})
    public void handleClientAbort(Exception e) {
        log.debug("Client disconnected or aborted request: {}", e.getClass().getSimpleName());
    }

    @ExceptionHandler(value = ApiException.class)
    public ResponseEntity<ApiError> handleApiException(ApiException e) {
        return apiErrorResponse(e.getHttpStatus(), e.getMessageCode(), e.getArgs());
    }

    @ExceptionHandler(InternalAuthenticationServiceException.class)
    public ResponseEntity<ApiError> handleInternalAuthError() {
        return apiErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, MessageCodes.AUTH_PRINCIPAL_ERROR);
    }

    //endregion

    //region 400 Series Handlers

    //    400 - validation & bad input
    @ExceptionHandler(value = IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleBadRequest() {
        return apiErrorResponse(HttpStatus.BAD_REQUEST, MessageCodes.BAD_REQUEST);
    }

    //    401 - unauthorized
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiError> handleAuthenticationError() {
        return apiErrorResponse(HttpStatus.UNAUTHORIZED, MessageCodes.AUTHENTICATION_REQUIRED);
    }

    //    404 - not found
    @ExceptionHandler(value = {EntityNotFoundException.class, NoResourceFoundException.class})
    public ResponseEntity<ApiError> handleNotFound() {
        return apiErrorResponse(HttpStatus.NOT_FOUND, MessageCodes.NOT_FOUND);
    }

    //    409 - conflicts
    @ExceptionHandler(value = DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleConflict() {
        return apiErrorResponse(HttpStatus.CONFLICT, MessageCodes.CONFLICT);
    }

    //    413 - payload too large
    @ExceptionHandler(value = MaxUploadSizeExceededException.class)
    public ResponseEntity<ApiError> handlePayloadTooLarge() {
        return apiErrorResponse(HttpStatus.CONTENT_TOO_LARGE, MessageCodes.PAYLOAD_TOO_LARGE);
    }

    //endregion

    //region 500 Series Handlers

    //    500 - unexpected error
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleUnexpected(Exception e) {
        log.error("unexpected exception caught in GlobalExceptionHandler", e);
        return apiErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, MessageCodes.UNEXPECTED_ERROR);
    }

    //endregion

    // region Helpers

    private ResponseEntity<ApiError> apiErrorResponse(HttpStatus status, String messageCode, Object... args) {
        String message = messageUtil.getMessage(messageCode, args);
        return ApiError.response(status, message);
    }

    // endregion

}
