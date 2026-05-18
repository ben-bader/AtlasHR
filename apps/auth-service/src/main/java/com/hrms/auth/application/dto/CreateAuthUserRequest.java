package com.hrms.auth.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Request DTO for Admin User Creation
 * 
 * Used by Employee Service to create auth users
 * when employees are onboarded
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateAuthUserRequest {

    /**
     * Employee ID from Employee Service
     * This becomes the login username
     */
    private String employeeId;

    /**
     * Initial password for the user
     * Should be hashed before storage
     */
    private String password;

    /**
     * Optional: employee email for reference
     */
    private String email;

    /**
     * Optional: employee first name
     */
    private String firstName;

    /**
     * Optional: employee last name
     */
    private String lastName;

}
