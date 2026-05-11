package com.hrms.attendance_service.application.dto;

import com.hrms.attendance_service.common.enums.PlanningStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class ShiftPlanningResponseDTO {

    private Long id;

    private String employeeId;

    private String employeeName;

    private String departmentName;

    private String designationName;

    private Long shiftId;

    private String shiftName;

    private LocalDate startDate;

    private LocalDate endDate;

    private Boolean saturdayOff;

    private Boolean sundayOff;

    private PlanningStatus status;

    private String note;
}
