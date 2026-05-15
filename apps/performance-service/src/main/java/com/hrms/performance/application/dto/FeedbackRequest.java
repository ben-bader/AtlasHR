package com.hrms.performance.application.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import com.hrms.performance.common.enums.FeedbackType;

import lombok.Data;

@Data
public class FeedbackRequest {

    @NotBlank(message = "Employee ID is required")
    private String employeeId;

    @NotBlank(message = "Manager ID is required")
    private String managerId;

    @NotBlank(message = "Cycle ID is required")
    private String cycleId;

    @NotBlank(message = "Comment is required")
    private String comment;

    @NotNull(message = "Rating is required")
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must be 5 or less")
    private Integer rating;

    @NotNull(message = "Feedback type is required")
    private FeedbackType type;
}
