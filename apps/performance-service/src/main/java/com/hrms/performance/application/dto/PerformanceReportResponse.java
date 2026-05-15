package com.hrms.performance.application.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class PerformanceReportResponse {

    private String employeeId;
    private int totalGoals;
    private int completedGoals;
    private int selfAppraisals;
    private int managerAppraisals;
    private int feedbackCount;
    private LocalDateTime lastUpdated;
}
