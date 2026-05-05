package com.hrms.employee.common.constants;

/**
 * Constants used across the application
 */
public class ApplicationConstants {

    private ApplicationConstants() {
        // Private constructor to prevent instantiation
    }

    // Entity ID Prefixes
    public static final String EMPLOYEE_ID_PREFIX = "E";
    public static final String SKILL_ID_PREFIX = "S";
    public static final String DESIGNATION_ID_PREFIX = "D";
    public static final String INSURANCE_ID_PREFIX = "INS";
    public static final String EMPLOYMENT_HISTORY_PREFIX = "EH";
    public static final String ORGANIZATION_CHART_PREFIX = "OC";

    // ID Generation
    public static final int ID_NUMERIC_DIGITS = 6;
    public static final long MAX_ID_SEQUENCE = 999999L;

    // Default Values
    public static final int DEFAULT_PAGE_SIZE = 20;
    public static final int DEFAULT_PAGE_NUMBER = 0;

    // Status Messages
    public static final String ENTITY_CREATED = "Entity created successfully";
    public static final String ENTITY_UPDATED = "Entity updated successfully";
    public static final String ENTITY_DELETED = "Entity deleted successfully";
    public static final String ENTITY_NOT_FOUND = "Entity not found";

    // Error Messages
    public static final String EMPLOYEE_NOT_FOUND = "Employee not found";
    public static final String DEPARTMENT_NOT_FOUND = "Department not found";
    public static final String DEPARTMENT_HEAD_NOT_FOUND = "Department head employee not found";
    public static final String PARENT_DEPARTMENT_NOT_FOUND = "Parent department not found";
    public static final String DESIGNATION_NOT_FOUND = "Designation not found";
    public static final String INSURANCE_NOT_FOUND = "Insurance not found";
    public static final String SKILL_NOT_FOUND = "Skill not found";

}
