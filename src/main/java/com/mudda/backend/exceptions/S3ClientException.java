package com.mudda.backend.exceptions;

import lombok.Getter;

@Getter
public class S3ClientException extends RuntimeException {

    private final String errorMessageCode;
    private final Object[] args;

    public S3ClientException(String errorMessageCode, Object... args) {
        super(errorMessageCode);
        this.errorMessageCode = errorMessageCode;
        this.args = args;
    }

}
