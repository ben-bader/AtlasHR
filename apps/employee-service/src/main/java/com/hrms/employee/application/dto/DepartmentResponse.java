package com.hrms.employee.application.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DepartmentResponse {

    private Long departmentId;
    private String departmentName;
    private String description;
    private String departmentCode;
    private String departmentHead;
    private Long parentDepartmentId;
    private String status;

}
