package com.majed.acadlink.dto;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    @JsonProperty("data")
    private T data;

    @JsonProperty("error")
    private String error;

    @JsonProperty("status")
    private int status;

    public ApiResponse(T data, String error, int status) {
        this.data = data;
        this.error = error;
        this.status = status;
    }

    public T getData() {
        return data;
    }

    public String getError() {
        return error;
    }

    public int getStatus() {
        return status;
    }

    public static <T> ResponseEntity<ApiResponse<T>> success(T data, HttpStatus status) {
        return ResponseEntity.ok(new ApiResponse<>(data, null, status.value()));
    }

    public static <T> ResponseEntity<ApiResponse<T>> error(String error, HttpStatus status) {
        return ResponseEntity.status(status)
                .body(new ApiResponse<>(null, error, status.value()));
    }
}
