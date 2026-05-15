package com.hrms.performance.application.dto;

import java.time.LocalDateTime;

import com.hrms.performance.common.enums.AppraisalStatus;

import lombok.Data;

@Data
public class SelfAppraisalResponse {

    private String id;
    private String employeeId;
    private String cycleId;
    private String strengths;
    private String improvements;
    private String goalSummary;
    private AppraisalStatus status;
    private LocalDateTime submittedAt;
}
