package com.hrms.performance.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import com.hrms.performance.common.enums.AppraisalStatus;

import lombok.Data;

@Data
public class SelfAppraisalRequest {

    @NotBlank(message = "Employee ID is required")
    private String employeeId;

    @NotBlank(message = "Cycle ID is required")
    private String cycleId;

    @NotBlank(message = "Strengths are required")
    private String strengths;

    @NotBlank(message = "Improvements are required")
    private String improvements;

    private String goalSummary;

    @NotNull(message = "Status is required")
    private AppraisalStatus status;
}
