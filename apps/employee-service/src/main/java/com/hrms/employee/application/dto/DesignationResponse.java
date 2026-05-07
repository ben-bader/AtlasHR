package com.hrms.employee.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DesignationResponse {

    @JsonProperty("designationId")
    private String id;
    private String designationName;
    private String description;
    private String designationCode;
    private Integer hierarchyLevel;
    private String reportingDesignation;
    private String status;

}
