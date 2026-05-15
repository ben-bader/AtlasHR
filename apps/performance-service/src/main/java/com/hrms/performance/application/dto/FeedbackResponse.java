package com.hrms.performance.application.dto;

import java.time.LocalDateTime;

import com.hrms.performance.common.enums.FeedbackType;

import lombok.Data;

@Data
public class FeedbackResponse {

    private String id;
    private String employeeId;
    private String managerId;
    private String cycleId;
    private String comment;
    private Integer rating;
    private FeedbackType type;
    private LocalDateTime createdAt;
}
