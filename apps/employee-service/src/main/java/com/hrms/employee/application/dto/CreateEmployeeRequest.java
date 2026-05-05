package com.hrms.employee.application.dto;

import lombok.*;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateEmployeeRequest {

    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String aadharNumber;
    private String panNumber;

    private LocalDate dateOfBirth;
    private String gender;
    private String nationality;
    private String bloodGroup;
    private String maritalStatus;

    private String primaryPhone;
    private String alternatePhone;
    private String currentAddress;
    private String city;
    private String state;
    private String postalCode;

    private String accountHolderName;
    private String accountNumber;
    private String ifscCode;
    private String bankName;
    private String accountType;

    private String emergencyContactName;
    private String emergencyContactRelationship;
    private String emergencyContactPhone;
    private String emergencyContactEmail;

    // Insurance fields
    private String policyNumber;
    private String insuranceType;
    private String providerName;
    private Double coverageAmount;
    private LocalDate policyStartDate;
    private LocalDate policyEndDate;
    private Double premiumAmount;
    private String beneficiaryName;
    private String beneficiaryRelationship;
    private String beneficiaryPhone;
    private String beneficiaryEmail;

    private Long departmentId;
    private String designationId;
    private String reportingManagerId;
    private LocalDate joiningDate;
    private String grade;

}
