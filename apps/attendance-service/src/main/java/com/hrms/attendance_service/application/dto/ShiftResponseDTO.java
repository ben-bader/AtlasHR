package com.hrms.attendance_service.application.dto;

import com.hrms.attendance_service.common.enums.ShiftType;
import com.hrms.attendance_service.common.enums.VerificationMethod;
import com.hrms.attendance_service.common.enums.WorkDay;
import lombok.Data;

import java.time.LocalTime;
import java.util.List;

@Data
public class ShiftResponseDTO {

    private Long id;

    private String name;

    private String description;

    private ShiftType shiftType;

    private LocalTime startTime;

    private LocalTime endTime;

    private Integer gracePeriodMinutes;

    private Double minimumWorkingHours;

    private Double overtimeAfterHours;

    private Boolean active;

    private List<WorkDay> workingDays;

    private List<VerificationMethod> verificationMethods;
}
