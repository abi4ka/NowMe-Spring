package com.abik.nowme.module.shared.exception;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends ApiException {

    public ResourceNotFoundException(String errorCode) {
        super(HttpStatus.NOT_FOUND, errorCode);
    }

    public ResourceNotFoundException(String errorCode, Throwable cause) {
        super(HttpStatus.NOT_FOUND, errorCode, cause);
    }
}
