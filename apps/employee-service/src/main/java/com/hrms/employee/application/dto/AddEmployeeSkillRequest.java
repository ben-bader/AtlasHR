package com.hrms.employee.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddEmployeeSkillRequest {

    @JsonProperty("employeeId")
    private String id;
    private String skillName;
    private String competencyLevel;
    private String certification;

}
