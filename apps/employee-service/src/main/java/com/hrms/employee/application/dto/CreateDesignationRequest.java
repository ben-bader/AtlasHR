package com.hrms.employee.application.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateDesignationRequest {

    private String designationName;
    private String description;
    private String designationCode;
    private Integer hierarchyLevel;
    private String reportingDesignation;

}
