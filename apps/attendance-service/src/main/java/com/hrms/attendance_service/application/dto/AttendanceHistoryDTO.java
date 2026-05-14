package com.hrms.attendance_service.application.dto;

import com.hrms.attendance_service.common.enums.AttendanceAction;
import com.hrms.attendance_service.common.enums.VerificationMethod;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AttendanceHistoryDTO {

    private Long id;

    private Long attendanceId;

    private String employeeId;

    private LocalDateTime actionTime;

    private AttendanceAction action;

    private VerificationMethod method;

    private String description;

    private String oldValue;

    private String newValue;

    private String performedBy;

    private String deviceInfo;

    private String ipAddress;
}
