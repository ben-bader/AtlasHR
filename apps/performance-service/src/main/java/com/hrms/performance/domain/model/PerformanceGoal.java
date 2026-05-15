package com.hrms.performance.domain.model;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import com.hrms.performance.common.enums.GoalStatus;
import lombok.Data;

@Entity
@Table(name = "performance_goals")
@Data
public class PerformanceGoal {

    @Id
    private String id;
    private String employeeId;
    private String managerId;
    private String title;
    private String description;
    private LocalDate targetDate;

    @Enumerated(EnumType.STRING)
    private GoalStatus status;
    private int progress;
    private String cycleId;
}
