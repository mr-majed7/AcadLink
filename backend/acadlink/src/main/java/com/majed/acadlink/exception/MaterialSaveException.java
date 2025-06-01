package com.majed.acadlink.exception;

/**
 * Exception thrown when an error occurs during material saving operations.
 */
public class MaterialSaveException extends RuntimeException {
    
    /**
     * Constructs a new MaterialSaveException with the specified detail message.
     *
     * @param message the detail message
     */
    public MaterialSaveException(String message) {
        super(message);
    }

    /**
     * Constructs a new MaterialSaveException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause
     */
    public MaterialSaveException(String message, Throwable cause) {
        super(message, cause);
    }
} 