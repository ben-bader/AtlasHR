package com.hrms.employee.common.exception;

import com.hrms.common.exception.ResourceNotFoundException;

/**
 * Thrown when a requested entity does not exist.
 * Extends the common ResourceNotFoundException so the shared GlobalExceptionHandler
 * returns a 404 ApiResponse without any service-level handler.
 */
public class EntityNotFoundException extends ResourceNotFoundException {

    private final String entityName;
    private final String identifier;

    public EntityNotFoundException(String entityName, String identifier) {
        super(String.format("%s not found with identifier: %s", entityName, identifier));
        this.entityName = entityName;
        this.identifier = identifier;
    }

    public EntityNotFoundException(String entityName, Long identifier) {
        super(String.format("%s not found with ID: %d", entityName, identifier));
        this.entityName = entityName;
        this.identifier = String.valueOf(identifier);
    }

    public EntityNotFoundException(String message) {
        super(message);
        this.entityName = "Entity";
        this.identifier = "Unknown";
    }

    public String getEntityName() { return entityName; }
    public String getIdentifier() { return identifier; }
}
