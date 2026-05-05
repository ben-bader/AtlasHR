package com.hrms.employee.domain.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.hrms.employee.common.enums.EmploymentChangeType;

@Entity
@Table(name = "employment_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmploymentHistory {

    @Id
    @Column(length = 20)
    private String historyId;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(nullable = false)
    private LocalDate effectiveDate;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private EmploymentChangeType changeType;

    @ManyToOne
    @JoinColumn(name = "previous_designation_id")
    private Designation previousDesignation;

    @ManyToOne
    @JoinColumn(name = "new_designation_id")
    private Designation newDesignation;

    @ManyToOne
    @JoinColumn(name = "previous_department_id")
    private Department previousDepartment;

    @ManyToOne
    @JoinColumn(name = "new_department_id")
    private Department newDepartment;

    private String previousGrade;
    private String newGrade;

    @Column(length = 500)
    private String reason;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

}
