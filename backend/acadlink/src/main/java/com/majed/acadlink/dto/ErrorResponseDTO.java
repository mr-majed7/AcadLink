package com.majed.acadlink.dto;

public class ErrorResponseDTO {
    private final String message;
    private final int statusCode;

    public ErrorResponseDTO(String message, int statusCode) {
        this.message = message;
        this.statusCode = statusCode;
    }

    public String getMessage() {
        return message;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
