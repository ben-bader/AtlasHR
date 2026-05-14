package com.hrms.attendance_service.application.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class DailyAttendanceDTO {

    private Long id;

    private LocalDate date;

    private Integer totalPresent;

    private Integer totalAbsent;

    private Integer totalLate;

    private Integer totalOnLeave;

    private List<AttendanceResponseDTO> attendances;
}
