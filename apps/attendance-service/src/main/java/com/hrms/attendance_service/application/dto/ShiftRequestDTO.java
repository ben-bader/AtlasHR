package com.hrms.attendance_service.application.dto;

import com.hrms.attendance_service.common.enums.ShiftType;
import com.hrms.attendance_service.common.enums.VerificationMethod;
import com.hrms.attendance_service.common.enums.WorkDay;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalTime;
import java.util.List;

@Data
public class ShiftRequestDTO {

    @NotBlank
    private String name;

    private String description;

    @NotNull
    private LocalTime startTime;

    @NotNull
    private LocalTime endTime;

    private Integer gracePeriodMinutes;

    private Double minimumWorkingHours;

    private Double overtimeAfterHours;

    @NotNull
    private ShiftType shiftType;

    private List<WorkDay> workingDays;

    private List<VerificationMethod> verificationMethods;

    private Boolean active;
}
