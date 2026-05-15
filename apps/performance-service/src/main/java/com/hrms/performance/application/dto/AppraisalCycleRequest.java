package com.hrms.performance.application.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import com.hrms.performance.common.enums.AppraisalStatus;

import lombok.Data;

@Data
public class AppraisalCycleRequest {

    @NotBlank(message = "Cycle name is required")
    private String name;

    private String description;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    private LocalDate endDate;

    @NotNull(message = "Cycle status is required")
    private AppraisalStatus status;

    private String templateId;
}
