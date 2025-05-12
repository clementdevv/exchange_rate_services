package com.anvil_shield.rate_service.exception;

import com.anvil_shield.rate_service.io.ErrorResponse;

public class ExternalApiException extends RuntimeException {
    private final ErrorResponse errorResponse;

    public ExternalApiException(ErrorResponse errorResponse) {
        this.errorResponse = errorResponse;
    }

    public ErrorResponse getErrorResponse() {
        return errorResponse;
    }
}