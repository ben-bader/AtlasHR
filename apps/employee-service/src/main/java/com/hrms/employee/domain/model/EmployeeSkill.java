package com.hrms.employee.domain.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

import com.hrms.employee.common.enums.CompetencyLevel;
import com.hrms.employee.common.enums.SkillStatus;

@Entity
@Table(name = "employee_skills")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeSkill {

    @Id
    @Column(length = 20)
    private String skillId;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(nullable = false)
    private String skillName;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CompetencyLevel competencyLevel;

    @Column(length = 500)
    private String certification;

    private String certificationId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private SkillStatus status;

    @Column(nullable = false, updatable = false)
    private LocalDateTime acquiredDate;

    private LocalDateTime lastUpdatedDate;

    @PrePersist
    protected void onCreate() {
        if (acquiredDate == null) {
            acquiredDate = LocalDateTime.now();
        }
        if (lastUpdatedDate == null) {
            lastUpdatedDate = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        lastUpdatedDate = LocalDateTime.now();
    }

}
