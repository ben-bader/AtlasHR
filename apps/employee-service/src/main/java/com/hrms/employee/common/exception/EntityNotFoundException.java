package com.hrms.employee.common.exception;

/**
 * Custom exception for entity not found
 */
public class EntityNotFoundException extends RuntimeException {

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

    public String getEntityName() {
        return entityName;
    }

    public String getIdentifier() {
        return identifier;
    }

}
