package com.anvil_shield.rate_service.exception;

import com.anvil_shield.rate_service.io.ErrorResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    @Test
    void handleApiError_ReturnsBadRequestWithErrorResponse() {
        // Arrange
        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        ErrorResponse expectedResponse = new ErrorResponse();
        expectedResponse.setSuccess(false);
        ErrorResponse.ApiError apiError = new ErrorResponse.ApiError();
        apiError.setCode(400);
        apiError.setInfo("Test error");
        expectedResponse.setError(apiError);

        ExternalApiException exception = new ExternalApiException(expectedResponse);

        // Act
        ResponseEntity<?> response = handler.handleApiError(exception);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());
    }
}

