package com.hrms.employee.application.dto;

import lombok.*;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateEmployeeRequest {

    // Personal Information
    private String firstName;
    private String lastName;
    private String email;
    private String CIN; // Carte d'Identité Nationale

    private LocalDate dateOfBirth;
    private String gender;
    private String nationality;
    private String bloodGroup;
    private String maritalStatus;

    // Contact Information (Moroccan format)
    private String primaryPhone; // Moroccan format: 06XXXXXXXX or +212XXXXXXXXX
    private String alternatePhone;
    private String currentAddress;
    private String city; // Moroccan city
    private String province; // Moroccan province
    private String codePostal; // Moroccan postal code

    // Bank Details (RIB is Moroccan banking standard)
    private String accountHolderName;
    private String accountNumber;
    private String RIB; // Relevé d'Identité Bancaire (Moroccan standard)
    private String bankName;
    private String accountType;

    // Emergency Contact
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

    // Employment Information
    private Long departmentId;
    private String designationId;
    private String reportingManagerId;
    private LocalDate joiningDate;
    private String grade;

}
