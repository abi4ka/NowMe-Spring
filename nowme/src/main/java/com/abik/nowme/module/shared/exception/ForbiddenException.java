package com.abik.nowme.module.shared.exception;

import org.springframework.http.HttpStatus;

public class ForbiddenException extends ApiException {

    public ForbiddenException(String errorCode) {
        super(HttpStatus.FORBIDDEN, errorCode);
    }

    public ForbiddenException(String errorCode, Throwable cause) {
        super(HttpStatus.FORBIDDEN, errorCode, cause);
    }
}
