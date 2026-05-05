package com.hrms.employee.common.enums;

/**
 * Enum for insurance type
 */
public enum InsuranceType {
    HEALTH("Health"),
    LIFE("Life"),
    DENTAL("Dental"),
    VISION("Vision"),
    DISABILITY("Disability"),
    ACCIDENTAL("Accidental"),
    OTHER("Other");

    private final String displayName;

    InsuranceType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
