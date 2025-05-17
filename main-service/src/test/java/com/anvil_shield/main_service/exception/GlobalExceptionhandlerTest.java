package com.anvil_shield.main_service.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.server.ResponseStatusException;

public class GlobalExceptionhandlerTest {
    @InjectMocks
    private GlobalExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Should handle AccessDeniedException and return 403 status")
    void handleAccessDenied() {
        // Arrange
        String errorMessage = "Access denied";
        AccessDeniedException exception = new AccessDeniedException(errorMessage);

        // Act
        ResponseEntity<?> response = exceptionHandler.handleAccessDenied(exception);

        // Assert
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertTrue((Boolean) responseBody.get("error"));
        assertEquals(errorMessage, responseBody.get("message"));
    }

    @Test
    @DisplayName("Should handle AlreadyExistsException and return 409 status")
    void handleAlreadyExist() {
        // Arrange
        String errorMessage = "Resource already exists";
        AlreadyExistsException exception = new AlreadyExistsException(errorMessage);

        // Act
        ResponseEntity<?> response = exceptionHandler.handleAlreadyExist(exception);

        // Assert
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertTrue((Boolean) responseBody.get("error"));
        assertEquals(errorMessage, responseBody.get("message"));
    }

    @Test
    @DisplayName("Should handle ResourceNotFoundException and return 404 status")
    void handleResourceNotFoundException() {
        // Arrange
        String errorMessage = "Resource not found";
        ResourceNotFoundException exception = new ResourceNotFoundException(errorMessage);

        // Act
        ResponseEntity<?> response = exceptionHandler.handleResourceNotFoundException(exception);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertTrue((Boolean) responseBody.get("error"));
        assertEquals(errorMessage, responseBody.get("message"));
    }

    @Test
    @DisplayName("Should handle MethodArgumentNotValidException and return 400 status with field errors")
    void handleValidationException() {
        // Arrange
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        when(exception.getBindingResult()).thenReturn(bindingResult);
        
        List<FieldError> fieldErrors = new ArrayList<>();
        fieldErrors.add(new FieldError("object", "email", "Invalid email format"));
        fieldErrors.add(new FieldError("object", "password", "Password is required"));
        
        when(bindingResult.getFieldErrors()).thenReturn(fieldErrors);

        // Act
        ResponseEntity<?> response = exceptionHandler.handleValidationException(exception);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertTrue((Boolean) responseBody.get("error"));
        assertEquals("Validation failed", responseBody.get("message"));
        
        Map<String, String> errors = (Map<String, String>) responseBody.get("errors");
        assertNotNull(errors);
        assertEquals(2, errors.size());
        assertEquals("Invalid email format", errors.get("email"));
        assertEquals("Password is required", errors.get("password"));
    }

    @Test
    @DisplayName("Should handle ResponseStatusException and return appropriate status")
    void handleResponseStatusException() {
        // Arrange
        String errorMessage = "Bad request";
        ResponseStatusException exception = new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMessage);

        // Act
        ResponseEntity<?> response = exceptionHandler.handleResponseStatusException(exception);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertTrue((Boolean) responseBody.get("error"));
        assertEquals(errorMessage, responseBody.get("message"));
    }

    @Test
    @DisplayName("Should handle generic Exception and return 401 status")
    void handleGenericException() {
        // Arrange
        Exception exception = new RuntimeException("Some unexpected error");

        // Act
        ResponseEntity<?> response = exceptionHandler.handleGenericException(exception);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertTrue((Boolean) responseBody.get("error"));
        assertEquals("Authentication failed", responseBody.get("message"));
    }
}
