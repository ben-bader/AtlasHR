package com.hrms.employee.application.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeSkillResponse {

    private String skillId;
    private String employeeId;
    private String skillName;
    private String competencyLevel;
    private String certification;
    private String status;

}
