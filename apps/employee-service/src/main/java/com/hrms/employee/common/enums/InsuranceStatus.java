package com.hrms.employee.common.enums;

/**
 * Enum for insurance status
 */
public enum InsuranceStatus {
    ACTIVE("Active"),
    INACTIVE("Inactive"),
    CLAIM_PENDING("Claim Pending"),
    CLAIM_APPROVED("Claim Approved"),
    CLAIM_REJECTED("Claim Rejected"),
    EXPIRED("Expired");

    private final String displayName;

    InsuranceStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
