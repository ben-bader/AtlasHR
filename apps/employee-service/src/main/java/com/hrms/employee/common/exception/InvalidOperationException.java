package com.hrms.employee.common.exception;

import com.hrms.common.exception.BadRequestException;

/**
 * Thrown when a business operation is not allowed in the current state.
 * Extends the common BadRequestException so the shared GlobalExceptionHandler
 * returns a 400 ApiResponse without any service-level handler.
 */
public class InvalidOperationException extends BadRequestException {

    private final String operation;
    private final String reason;

    public InvalidOperationException(String operation, String reason) {
        super(String.format("Invalid operation '%s': %s", operation, reason));
        this.operation = operation;
        this.reason = reason;
    }

    public InvalidOperationException(String message) {
        super(message);
        this.operation = "Unknown";
        this.reason = message;
    }

    public String getOperation() { return operation; }
    public String getReason() { return reason; }
}
