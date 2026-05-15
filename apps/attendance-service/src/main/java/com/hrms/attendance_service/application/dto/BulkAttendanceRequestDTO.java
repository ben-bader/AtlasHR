package com.hrms.attendance_service.application.dto;

import lombok.Data;

import java.util.List;

@Data
public class BulkAttendanceRequestDTO {

    private String deviceId;

    private List<BulkAttendanceRecordDTO> records;
}
