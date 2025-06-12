package com.majed.acadlink.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class VerificationCodeExceptionTest {

    @Test
    void constructor_WithMessage_ShouldCreateException() {
        // Arrange
        String message = "Test verification code error";

        // Act
        VerificationCodeException exception = new VerificationCodeException(message);

        // Assert
        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
    }

    @Test
    void constructor_WithMessageAndCause_ShouldCreateException() {
        // Arrange
        String message = "Test verification code error";
        Throwable cause = new RuntimeException("Root cause");

        // Act
        VerificationCodeException exception = new VerificationCodeException(message, cause);

        // Assert
        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }
} 