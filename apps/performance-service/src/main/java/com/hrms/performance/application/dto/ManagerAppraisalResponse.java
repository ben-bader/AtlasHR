package com.hrms.performance.application.dto;

import java.time.LocalDateTime;

import com.hrms.performance.common.enums.AppraisalStatus;

import lombok.Data;

@Data
public class ManagerAppraisalResponse {

    private String id;
    private String employeeId;
    private String managerId;
    private String cycleId;
    private String summary;
    private Integer rating;
    private AppraisalStatus status;
    private LocalDateTime submittedAt;
}
