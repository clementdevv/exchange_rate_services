package com.anvil_shield.main_service.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.anvil_shield.main_service.io.ErrorResponse;

public class ExternalApiExceptionTest {
    @Test
    @DisplayName("Should create ExternalApiException with correct error response")
    void testConstructor() {
        // Arrange
        ErrorResponse errorResponse = new ErrorResponse();
        ErrorResponse.ApiError apiError = new ErrorResponse.ApiError();
        apiError.setCode(400);
        apiError.setInfo("API Error");
        errorResponse.setSuccess(false);
        errorResponse.setError(apiError);
        
        // Act
        ExternalApiException exception = new ExternalApiException(errorResponse);
        
        // Assert
        assertNotNull(exception.getErrorResponse());
        assertEquals(errorResponse, exception.getErrorResponse());
        assertEquals(false, exception.getErrorResponse().isSuccess());
        assertEquals("API Error", exception.getErrorResponse().getError().getInfo());
        assertEquals(400, exception.getErrorResponse().getError().getCode());
    }
}
