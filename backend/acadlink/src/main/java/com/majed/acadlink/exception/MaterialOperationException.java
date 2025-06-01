package com.majed.acadlink.exception;

/**
 * Exception thrown when an error occurs during material operations.
 */
public class MaterialOperationException extends RuntimeException {
    
    /**
     * Constructs a new MaterialOperationException with the specified detail message.
     *
     * @param message the detail message
     */
    public MaterialOperationException(String message) {
        super(message);
    }

    /**
     * Constructs a new MaterialOperationException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause
     */
    public MaterialOperationException(String message, Throwable cause) {
        super(message, cause);
    }
} 