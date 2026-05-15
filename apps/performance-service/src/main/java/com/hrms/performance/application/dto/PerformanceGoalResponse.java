package com.hrms.performance.application.dto;

import java.time.LocalDate;

import com.hrms.performance.common.enums.GoalStatus;

import lombok.Data;

@Data
public class PerformanceGoalResponse {

    private String id;
    private String employeeId;
    private String managerId;
    private String title;
    private String description;
    private LocalDate targetDate;
    private GoalStatus status;
    private int progress;
    private String cycleId;
}
