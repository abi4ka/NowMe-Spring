package com.abik.nowme.module.shared.exception;

import org.springframework.http.HttpStatus;

public class ConflictException extends ApiException {

    public ConflictException(String errorCode) {
        super(HttpStatus.CONFLICT, errorCode);
    }

    public ConflictException(String errorCode, Throwable cause) {
        super(HttpStatus.CONFLICT, errorCode, cause);
    }
}
