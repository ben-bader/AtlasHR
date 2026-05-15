package com.hrms.attendance_service.application.dto;

import com.hrms.attendance_service.common.enums.AttendanceType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BulkAttendanceRecordDTO {

    private String employeeId;

    private LocalDateTime timestamp;

    private AttendanceType type; // IN / OUT
}
