package com.abik.nowme.module.shared.exception;

import org.springframework.http.HttpStatus;

public class UnauthorizedException extends ApiException {

    public UnauthorizedException(String errorCode) {
        super(HttpStatus.UNAUTHORIZED, errorCode);
    }

    public UnauthorizedException(String errorCode, Throwable cause) {
        super(HttpStatus.UNAUTHORIZED, errorCode, cause);
    }
}
