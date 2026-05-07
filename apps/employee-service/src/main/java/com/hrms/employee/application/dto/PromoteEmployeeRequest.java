package com.hrms.employee.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PromoteEmployeeRequest {

    @JsonProperty("employeeId")
    private String id;
    private String newDesignationId;
    private String newGrade;
    private LocalDate effectiveDate;
    private String reason;
    private Boolean triggerSalaryRevision;

}
