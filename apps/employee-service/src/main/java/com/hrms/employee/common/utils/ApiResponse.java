package com.hrms.employee.common.utils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Generic API Response wrapper for all endpoints
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiResponse<T> {

    private boolean success;
    private String message;
    private T data;
    private String timestamp;
    private String code;

    /**
     * Create a successful response
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .timestamp(String.valueOf(System.currentTimeMillis()))
                .code("SUCCESS")
                .build();
    }

    /**
     * Create an error response
     */
    public static <T> ApiResponse<T> error(String message, String code) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .timestamp(String.valueOf(System.currentTimeMillis()))
                .code(code)
                .build();
    }

    /**
     * Create an error response with data
     */
    public static <T> ApiResponse<T> error(String message, String code, T data) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .data(data)
                .timestamp(String.valueOf(System.currentTimeMillis()))
                .code(code)
                .build();
    }

}
