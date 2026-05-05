package com.hrms.employee.common.enums;

/**
 * Enum for employment status
 */
public enum EmploymentStatus {
    ACTIVE("Active"),
    INACTIVE("Inactive"),
    ON_LEAVE("On Leave"),
    SUSPENDED("Suspended"),
    TERMINATED("Terminated");

    private final String displayName;

    EmploymentStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
