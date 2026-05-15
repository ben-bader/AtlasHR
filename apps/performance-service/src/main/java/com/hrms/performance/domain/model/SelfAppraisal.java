package com.hrms.performance.domain.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import com.hrms.performance.common.enums.AppraisalStatus;
import lombok.Data;

@Entity
@Table(name = "self_appraisals")
@Data
public class SelfAppraisal {

    @Id
    private String id;
    private String employeeId;
    private String cycleId;
    private String strengths;
    private String improvements;
    private String goalSummary;

    @Enumerated(EnumType.STRING)
    private AppraisalStatus status;
    private LocalDateTime submittedAt;
}
