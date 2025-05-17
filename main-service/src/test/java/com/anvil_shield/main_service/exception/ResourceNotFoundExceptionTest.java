package com.anvil_shield.main_service.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ResourceNotFoundExceptionTest {
    @Test
    @DisplayName("Should create ResourceNotFoundException with correct message")
    void testConstructor() {
        // Arrange
        String errorMessage = "Resource not found";
        
        // Act
        ResourceNotFoundException exception = new ResourceNotFoundException(errorMessage);
        
        // Assert
        assertEquals(errorMessage, exception.getMessage());
    }
}
