package com.hrms.employee.application.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateDepartmentRequest {

    private String departmentName;
    private String description;
    private Long parentDepartmentId;
    private String departmentCode;
    private String departmentHead;

}
