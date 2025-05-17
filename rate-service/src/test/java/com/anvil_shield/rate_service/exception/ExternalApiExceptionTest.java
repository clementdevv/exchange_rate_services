package com.anvil_shield.rate_service.exception;

import com.anvil_shield.rate_service.io.ErrorResponse;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ExternalApiExceptionTest {
    @Test
    public void constructor_WithErrorResponse_StoresErrorResponse() {
        // Arrange
        ErrorResponse errorResponse = new ErrorResponse();
        ErrorResponse.ApiError apiError = new ErrorResponse.ApiError();
        apiError.setCode(404);
        apiError.setInfo("Resource not found");
        errorResponse.setSuccess(false);
        errorResponse.setError(apiError);

        // Act
        ExternalApiException exception = new ExternalApiException(errorResponse);

        // Assert
        assertNotNull(exception.getErrorResponse(), "Error response should not be null");
        assertEquals(errorResponse, exception.getErrorResponse(), "Error response should match the provided one");
        assertFalse(exception.getErrorResponse().isSuccess(), "Success flag should be false");
        assertEquals(404, exception.getErrorResponse().getError().getCode(), "Error code should be 404");
        assertEquals("Resource not found", exception.getErrorResponse().getError().getInfo(), "Error info should match");
    }

    @Test
    public void getErrorResponse_AfterConstruction_ReturnsOriginalErrorResponse() {
        // Arrange
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setSuccess(false);
        ErrorResponse.ApiError apiError = new ErrorResponse.ApiError();
        apiError.setCode(429);
        apiError.setInfo("Too many requests");
        errorResponse.setError(apiError);

        ExternalApiException exception = new ExternalApiException(errorResponse);

        // Act
        ErrorResponse retrievedResponse = exception.getErrorResponse();

        // Assert
        assertSame(errorResponse, retrievedResponse, "Retrieved error response should be the same instance");
    }
}
