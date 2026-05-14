package com.hrms.employee.application.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeInsuranceResponse {

    @JsonProperty("insuranceId")
    private String id;
    private String employeeId;
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
    private String status;
    private String claimDetails;
    private LocalDate claimDate;
    private Double claimAmount;

}
