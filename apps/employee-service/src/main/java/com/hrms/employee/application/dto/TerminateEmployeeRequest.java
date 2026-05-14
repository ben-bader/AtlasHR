package com.hrms.employee.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TerminateEmployeeRequest {

    @JsonProperty("employeeId")
    private String id;
    private LocalDate lastWorkingDate;
    private String terminationReason;
    private String comments;

}
