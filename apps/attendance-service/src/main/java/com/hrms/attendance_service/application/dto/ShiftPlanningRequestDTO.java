package com.hrms.attendance_service.application.dto;

import com.hrms.attendance_service.common.enums.PlanningStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ShiftPlanningRequestDTO {

    @NotBlank
    private String employeeId;

    @NotNull
    private Long shiftId;

    @NotNull
    private LocalDate startDate;

    @NotNull
    private LocalDate endDate;

    private Boolean saturdayOff;

    private Boolean sundayOff;

    private PlanningStatus status;

    private String note;
}
