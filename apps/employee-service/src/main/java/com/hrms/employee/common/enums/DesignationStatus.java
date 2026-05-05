package com.hrms.employee.common.enums;

/**
 * Enum for designation status
 */
public enum DesignationStatus {
    ACTIVE("Active"),
    DEPRECATED("Deprecated"),
    INACTIVE("Inactive");

    private final String displayName;

    DesignationStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
