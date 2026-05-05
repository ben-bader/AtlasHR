package com.hrms.employee.application.dto;

import lombok.*;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PromoteEmployeeRequest {

    private String employeeId;
    private String newDesignationId;
    private String newGrade;
    private LocalDate effectiveDate;
    private String reason;
    private Boolean triggerSalaryRevision;

}
