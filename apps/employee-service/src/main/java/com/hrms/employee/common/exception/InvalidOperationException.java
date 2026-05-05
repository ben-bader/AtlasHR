package com.hrms.employee.common.exception;

/**
 * Custom exception for invalid operations
 */
public class InvalidOperationException extends RuntimeException {

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

    public String getOperation() {
        return operation;
    }

    public String getReason() {
        return reason;
    }

}
