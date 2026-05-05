package com.hrms.employee.common.enums;

/**
 * Enum for department status
 */
public enum DepartmentStatus {
    ACTIVE("Active"),
    ARCHIVED("Archived"),
    INACTIVE("Inactive");

    private final String displayName;

    DepartmentStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
