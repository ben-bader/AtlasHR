package com.hrms.employee.domain.model;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;

import com.hrms.employee.common.enums.InsuranceType;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InsuranceDetails {

    private String policyNumber;

    @Enumerated(EnumType.STRING)
    private InsuranceType insuranceType;

    private String providerName;

    private Double coverageAmount;

    private LocalDate policyStartDate;

    private LocalDate policyEndDate;

    private Double premiumAmount;

    private String beneficiaryName;

    private String beneficiaryRelationship;

}
