package com.hrms.employee.domain.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.hrms.employee.common.enums.InsuranceStatus;
import com.hrms.employee.common.enums.InsuranceType;

@Entity
@Table(name = "employee_insurances")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeInsurance {

    @Id
    @Column(length = 20)
    private String insuranceId;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(nullable = false)
    private String policyNumber;

    @Column(nullable = false, unique = true)
    private String policyNumberUnique;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private InsuranceType insuranceType;

    @Column(nullable = false)
    private String providerName;

    @Column(nullable = false)
    private Double coverageAmount;

    @Column(nullable = false)
    private LocalDate policyStartDate;

    private LocalDate policyEndDate;

    @Column(nullable = false)
    private Double premiumAmount;

    private String beneficiaryName;

    private String beneficiaryRelationship;

    private String beneficiaryPhone;

    private String beneficiaryEmail;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private InsuranceStatus status;

    @Column(length = 500)
    private String claimDetails;

    private LocalDate claimDate;

    private Double claimAmount;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = InsuranceStatus.ACTIVE;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

}
