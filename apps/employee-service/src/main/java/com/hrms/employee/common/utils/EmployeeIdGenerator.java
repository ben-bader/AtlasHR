package com.hrms.employee.common.utils;

import com.hrms.employee.common.constants.ApplicationConstants;
import org.springframework.stereotype.Component;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Generates employee IDs in format: E + 6 digits (e.g., E000001, E000002)
 * Uses ApplicationConstants for centralized configuration
 */
@Component
public class EmployeeIdGenerator {

    private final AtomicLong counter = new AtomicLong(0);

    /**
     * Generate the next employee ID
     * @return Employee ID in format EXXXXXX (e.g., E000001)
     */
    public String generateId() {
        long next = counter.incrementAndGet();
        if (next > ApplicationConstants.MAX_ID_SEQUENCE) {
            throw new RuntimeException("Employee ID sequence exceeded maximum value: " + ApplicationConstants.MAX_ID_SEQUENCE);
        }
        return String.format("%s%0" + ApplicationConstants.ID_NUMERIC_DIGITS + "d", 
            ApplicationConstants.EMPLOYEE_ID_PREFIX, next);
    }

    /**
     * Generate employee ID from a numeric value
     * @param number the numeric part
     * @return Employee ID in format EXXXXXX
     */
    public String generateId(long number) {
        if (number <= 0 || number > ApplicationConstants.MAX_ID_SEQUENCE) {
            throw new IllegalArgumentException("Number must be between 1 and " + ApplicationConstants.MAX_ID_SEQUENCE);
        }
        return String.format("%s%0" + ApplicationConstants.ID_NUMERIC_DIGITS + "d", 
            ApplicationConstants.EMPLOYEE_ID_PREFIX, number);
    }

    /**
     * Reset the counter (useful for testing)
     */
    public void reset() {
        counter.set(0);
    }

    /**
     * Get the next sequence number without incrementing
     */
    public long getNextSequence() {
        return counter.get() + 1;
    }

}
