package com.hrms.employee.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DepartmentResponse {

    @JsonProperty("departmentId")
    private Long id;
    private String departmentName;
    private String description;
    private String departmentCode;
    private String departmentHead;
    private Long parentDepartmentId;
    private String status;

}
