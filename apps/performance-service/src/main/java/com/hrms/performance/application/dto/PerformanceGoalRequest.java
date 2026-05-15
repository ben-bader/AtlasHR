package com.hrms.performance.application.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import com.hrms.performance.common.enums.GoalStatus;

import lombok.Data;

@Data
public class PerformanceGoalRequest {

    @NotBlank(message = "Employee ID is required")
    private String employeeId;

    @NotBlank(message = "Manager ID is required")
    private String managerId;

    @NotBlank(message = "Goal title is required")
    private String title;

    private String description;

    @NotNull(message = "Target date is required")
    private LocalDate targetDate;

    @NotNull(message = "Goal status is required")
    private GoalStatus status;

    private int progress;
    private String cycleId;
}
