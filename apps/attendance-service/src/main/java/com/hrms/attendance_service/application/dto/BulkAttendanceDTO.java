package com.hrms.attendance_service.application.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.*;

@Getter
@Setter
public class BulkAttendanceDTO {

    private String employeeId;

    private LocalDate date;

    private LocalDateTime checkIn;

    private LocalDateTime checkOut;
}
