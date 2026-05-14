package com.hrms.attendance_service.application.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
public class BulkAttendanceDTO {

    @NotBlank
    private String employeeId;

    @NotNull
    private LocalDate date;

    private LocalDateTime checkIn;

    private LocalDateTime checkOut;
}
