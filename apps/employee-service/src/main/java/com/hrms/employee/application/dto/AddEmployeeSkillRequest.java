package com.hrms.employee.application.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddEmployeeSkillRequest {

    private String employeeId;
    private String skillName;
    private String competencyLevel;
    private String certification;

}
