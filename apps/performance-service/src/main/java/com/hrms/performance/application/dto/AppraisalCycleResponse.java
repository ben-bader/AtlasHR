package com.hrms.performance.application.dto;

import java.time.LocalDate;

import com.hrms.performance.common.enums.AppraisalStatus;

import lombok.Data;

@Data
public class AppraisalCycleResponse {

    private String id;
    private String name;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private AppraisalStatus status;
    private String templateId;
}
