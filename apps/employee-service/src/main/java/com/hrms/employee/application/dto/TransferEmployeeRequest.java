package com.hrms.employee.application.dto;

import lombok.*;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransferEmployeeRequest {

    private String employeeId;
    private Long newDepartmentId;
    private String newDesignationId;
    private LocalDate effectiveDate;
    private String reason;

}
