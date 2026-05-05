package com.hrms.employee.application.dto;

import lombok.*;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TerminateEmployeeRequest {

    private String employeeId;
    private LocalDate lastWorkingDate;
    private String terminationReason;
    private String comments;

}
