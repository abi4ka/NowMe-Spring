package com.abik.nowme.module.shared.exception;

import org.springframework.http.HttpStatus;

public class BadRequestException extends ApiException {

    public BadRequestException(String errorCode) {
        super(HttpStatus.BAD_REQUEST, errorCode);
    }

    public BadRequestException(String errorCode, Throwable cause) {
        super(HttpStatus.BAD_REQUEST, errorCode, cause);
    }
}
