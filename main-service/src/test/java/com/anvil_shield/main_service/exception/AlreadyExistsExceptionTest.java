package com.anvil_shield.main_service.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AlreadyExistsExceptionTest {

    @Test
    @DisplayName("Should create AlreadyExistsException with correct message")
    void testConstructor() {
        // Arrange
        String errorMessage = "User already exists";
        
        // Act
        AlreadyExistsException exception = new AlreadyExistsException(errorMessage);
        
        // Assert
        assertEquals(errorMessage, exception.getMessage());
    }
}
