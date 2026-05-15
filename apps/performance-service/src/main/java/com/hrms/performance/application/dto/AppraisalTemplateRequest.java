package com.hrms.performance.application.dto;

import jakarta.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class AppraisalTemplateRequest {

    @NotBlank(message = "Template name is required")
    private String name;

    private String description;

    @NotBlank(message = "Template criteria is required")
    private String criteria;
}
