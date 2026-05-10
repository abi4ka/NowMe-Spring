package com.abik.nowme.module.shared.controller;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<Map<String, String>> handleExpiredToken(TokenExpiredException e) {
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, "TOKEN_EXPIRED");
    }

    @ExceptionHandler(JWTVerificationException.class)
    public ResponseEntity<Map<String, String>> handleInvalidToken(JWTVerificationException e) {
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, "INVALID_TOKEN");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        List<String> details = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::formatFieldError)
                .toList();

        return buildValidationErrorResponse(details);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, Object>> handleConstraintViolation(ConstraintViolationException e) {
        List<String> details = e.getConstraintViolations()
                .stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .toList();

        return buildValidationErrorResponse(details);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, String>> handleResponseStatusException(ResponseStatusException e) {
        String errorCode = e.getReason() == null || e.getReason().isBlank()
                ? e.getStatusCode().toString()
                : e.getReason();

        return buildErrorResponse(e.getStatusCode(), errorCode);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException e) {
        ErrorDescriptor error = mapRuntimeException(e);

        return buildErrorResponse(error.status(), error.errorCode());
    }

    private ResponseEntity<Map<String, String>> buildErrorResponse(HttpStatus status, String errorCode) {
        return ResponseEntity.status(status)
                .body(Map.of("error", errorCode));
    }

    private ResponseEntity<Map<String, String>> buildErrorResponse(org.springframework.http.HttpStatusCode status, String errorCode) {
        return ResponseEntity.status(status)
                .body(Map.of("error", errorCode));
    }

    private ResponseEntity<Map<String, Object>> buildValidationErrorResponse(List<String> details) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("error", "INVALID_REQUEST");
        body.put("details", details);

        return ResponseEntity.badRequest().body(body);
    }

    private String formatFieldError(FieldError fieldError) {
        return fieldError.getField() + ": " + fieldError.getDefaultMessage();
    }

    private ErrorDescriptor mapRuntimeException(RuntimeException e) {
        String message = e.getMessage();
        if (message == null || message.isBlank()) {
            return new ErrorDescriptor(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR");
        }

        return switch (message) {
            case "USER_NOT_FOUND", "PROFILE_USER_NOT_FOUND", "NOWME_NOT_FOUND", "FILE_NOT_FOUND",
                 "USER_TO_FOLLOW_NOT_FOUND", "USER_TO_UNFOLLOW_NOT_FOUND" ->
                    new ErrorDescriptor(HttpStatus.NOT_FOUND, message);
            case "ACCESS_DENIED", "IMAGE_ACCESS_DENIED" ->
                    new ErrorDescriptor(HttpStatus.FORBIDDEN, message);
            case "WRONG_PASSWORD", "TOKEN_REQUIRED", "INVALID_TOKEN",
                 "INVALID_REFRESH_TOKEN", "TOKEN_MISMATCH" ->
                    new ErrorDescriptor(HttpStatus.UNAUTHORIZED, message);
            case "ALREADY_LIKED", "USERNAME_ALREADY_EXISTS",
                 "CANNOT_FOLLOW_YOURSELF", "ALREADY_FOLLOWING",
                 "YOU_ARE_NOT_FOLLOWING_THIS_USER" ->
                    new ErrorDescriptor(HttpStatus.BAD_REQUEST, message);
            default -> new ErrorDescriptor(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR");
        };
    }

    private record ErrorDescriptor(HttpStatus status, String errorCode) {
    }
}
