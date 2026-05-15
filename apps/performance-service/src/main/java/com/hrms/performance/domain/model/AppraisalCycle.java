package com.hrms.performance.domain.model;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import com.hrms.performance.common.enums.AppraisalStatus;
import lombok.Data;

@Entity
@Table(name = "appraisal_cycles")
@Data
public class AppraisalCycle {

    @Id
    private String id;
    private String name;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    private AppraisalStatus status;
    private String templateId;
}
