package com.hrms.employee.application.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DesignationResponse {

    private String designationId;
    private String designationName;
    private String description;
    private String designationCode;
    private Integer hierarchyLevel;
    private String reportingDesignation;
    private String status;

}
