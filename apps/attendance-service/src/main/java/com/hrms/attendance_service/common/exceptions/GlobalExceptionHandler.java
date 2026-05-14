package com.hrms.attendance_service.common.exceptions;

import com.hrms.attendance_service.common.api.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // ================= VALIDATION =================
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<?> handleValidation(MethodArgumentNotValidException ex) {

        String error = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(f -> f.getField() + " : " + f.getDefaultMessage())
                .collect(Collectors.joining(", "));

        return ApiResponse.error(error, "VALIDATION_ERROR");
    }

    // ================= NOT FOUND =================
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiResponse<?> handleNotFound(ResourceNotFoundException ex) {

        return ApiResponse.error(
                ex.getMessage(),
                "RESOURCE_NOT_FOUND"
        );
    }

    // ================= BAD REQUEST =================
    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<?> handleBadRequest(BadRequestException ex) {

        return ApiResponse.error(
                ex.getMessage(),
                "BAD_REQUEST"
        );
    }

    // ================= GENERAL =================
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse<?> handleException(Exception ex) {

        return ApiResponse.error(
                "Internal server error",
                "INTERNAL_SERVER_ERROR"
        );
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<?> handleValidationException(ValidationException ex) {

        return ApiResponse.error(
                ex.getMessage(),
                "VALIDATION_EXCEPTION"
        );
    }
}
