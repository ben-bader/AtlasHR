package com.hrms.employee.common.enums;

/**
 * Enum for skill status
 */
public enum SkillStatus {
    ACTIVE("Active"),
    INACTIVE("Inactive"),
    ARCHIVED("Archived");

    private final String displayName;

    SkillStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
