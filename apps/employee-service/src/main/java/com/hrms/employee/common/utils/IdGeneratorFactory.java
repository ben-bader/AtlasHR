package com.hrms.employee.common.utils;

import com.hrms.employee.common.constants.ApplicationConstants;
import org.springframework.stereotype.Component;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Factory for generating IDs for various entities
 * - Employee: E + 6 digits (E000001)
 * - Skill: S + 6 digits (S000001)
 * - Department: Numeric Long ID
 * - Designation: D + 6 digits (D000001)
 * - Insurance: INS + 6 digits (INS000001)
 * - Employment History: EH + 6 digits (EH000001)
 * - Organization Chart: OC + 6 digits (OC000001)
 */
@Component
public class IdGeneratorFactory {

    private final AtomicLong employeeSequence = new AtomicLong(0);
    private final AtomicLong skillSequence = new AtomicLong(0);
    private final AtomicLong departmentSequence = new AtomicLong(0);
    private final AtomicLong designationSequence = new AtomicLong(0);
    private final AtomicLong insuranceSequence = new AtomicLong(0);
    private final AtomicLong employmentHistorySequence = new AtomicLong(0);
    private final AtomicLong organizationChartSequence = new AtomicLong(0);

    /**
     * Generate Employee ID: E + 6 digits
     */
    public String generateEmployeeId() {
        long next = employeeSequence.incrementAndGet();
        validateSequence(next);
        return String.format("%s%0" + ApplicationConstants.ID_NUMERIC_DIGITS + "d", 
            ApplicationConstants.EMPLOYEE_ID_PREFIX, next);
    }

    /**
     * Generate Skill ID: S + 6 digits
     */
    public String generateSkillId() {
        long next = skillSequence.incrementAndGet();
        validateSequence(next);
        return String.format("%s%0" + ApplicationConstants.ID_NUMERIC_DIGITS + "d", 
            ApplicationConstants.SKILL_ID_PREFIX, next);
    }

    /**
     * Generate Department ID: Numeric Long
     */
    public Long generateDepartmentId() {
        return departmentSequence.incrementAndGet();
    }

    /**
     * Generate Designation ID: D + 6 digits
     */
    public String generateDesignationId() {
        long next = designationSequence.incrementAndGet();
        validateSequence(next);
        return String.format("%s%0" + ApplicationConstants.ID_NUMERIC_DIGITS + "d", 
            ApplicationConstants.DESIGNATION_ID_PREFIX, next);
    }

    /**
     * Generate Insurance ID: INS + 6 digits
     */
    public String generateInsuranceId() {
        long next = insuranceSequence.incrementAndGet();
        validateSequence(next);
        return String.format("%s%0" + ApplicationConstants.ID_NUMERIC_DIGITS + "d", 
            ApplicationConstants.INSURANCE_ID_PREFIX, next);
    }

    /**
     * Generate Employment History ID: EH + 6 digits
     */
    public String generateEmploymentHistoryId() {
        long next = employmentHistorySequence.incrementAndGet();
        validateSequence(next);
        return String.format("%s%0" + ApplicationConstants.ID_NUMERIC_DIGITS + "d", 
            ApplicationConstants.EMPLOYMENT_HISTORY_PREFIX, next);
    }

    /**
     * Generate Organization Chart ID: OC + 6 digits
     */
    public String generateOrganizationChartId() {
        long next = organizationChartSequence.incrementAndGet();
        validateSequence(next);
        return String.format("%s%0" + ApplicationConstants.ID_NUMERIC_DIGITS + "d", 
            ApplicationConstants.ORGANIZATION_CHART_PREFIX, next);
    }

    private void validateSequence(long next) {
        if (next > ApplicationConstants.MAX_ID_SEQUENCE) {
            throw new RuntimeException("ID sequence exceeded maximum value: " + ApplicationConstants.MAX_ID_SEQUENCE);
        }
    }

    // Utility methods for resetting (mainly for testing)
    public void resetAll() {
        employeeSequence.set(0);
        skillSequence.set(0);
        departmentSequence.set(0);
        designationSequence.set(0);
        insuranceSequence.set(0);
        employmentHistorySequence.set(0);
        organizationChartSequence.set(0);
    }

}
