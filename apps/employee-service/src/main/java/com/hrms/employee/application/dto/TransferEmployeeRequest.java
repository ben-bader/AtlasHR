package com.hrms.employee.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransferEmployeeRequest {

    @JsonProperty("employeeId")
    private String id;
    private Long newDepartmentId;
    private String newDesignationId;
    private LocalDate effectiveDate;
    private String reason;

}
