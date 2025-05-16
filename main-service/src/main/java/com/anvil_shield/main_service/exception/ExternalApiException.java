package com.anvil_shield.main_service.exception;

import com.anvil_shield.main_service.io.ErrorResponse;

public class ExternalApiException extends RuntimeException {
    private final ErrorResponse errorResponse;

    public ExternalApiException(ErrorResponse errorResponse) {
        this.errorResponse = errorResponse;
    }

    public ErrorResponse getErrorResponse() {
        return errorResponse;
    }
}