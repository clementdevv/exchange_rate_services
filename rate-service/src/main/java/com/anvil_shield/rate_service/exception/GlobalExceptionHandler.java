package com.anvil_shield.rate_service.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ExternalApiException.class)
    public ResponseEntity<?> handleApiError(ExternalApiException ex) {
        return ResponseEntity.badRequest().body(ex.getErrorResponse());
    }
}