package com.hrms.attendance_service.application.dto;

import com.hrms.attendance_service.common.enums.AttendanceStatus;
import com.hrms.attendance_service.common.enums.VerificationMethod;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class AttendanceResponseDTO {

    private Long id;

    private String employeeId;

    private LocalDate date;

    private LocalDateTime checkIn;

    private LocalDateTime checkOut;

    private AttendanceStatus status;

    private Long shiftId;
    
    private String shiftName;

    private VerificationMethod method;

    private Double workedHours;

    private Boolean isLate;

    private Integer lateMinutes;

    private Boolean isOvertime;

    private Integer overtimeMinutes;
}
