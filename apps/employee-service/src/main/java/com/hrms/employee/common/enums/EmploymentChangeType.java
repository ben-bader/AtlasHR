package com.hrms.employee.common.enums;

/**
 * Enum for employment change type
 */
public enum EmploymentChangeType {
    PROMOTION("Promotion"),
    TRANSFER("Transfer"),
    DEMOTION("Demotion"),
    LATERAL_MOVE("Lateral Move"),
    SALARY_REVISION("Salary Revision"),
    GRADE_CHANGE("Grade Change");

    private final String displayName;

    EmploymentChangeType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
