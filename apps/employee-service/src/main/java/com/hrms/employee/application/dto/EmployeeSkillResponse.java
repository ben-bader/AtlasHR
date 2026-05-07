package com.hrms.employee.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeSkillResponse {

    @JsonProperty("skillId")
    private String id;
    private String employeeId;
    private String skillName;
    private String competencyLevel;
    private String certification;
    private String status;

}
