package com.hrms.employee.infrastructure.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeTransferredEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    private String employeeId;
    private String employeeName;
    private String previousDepartmentId;
    private String newDepartmentId;
    private String previousDesignation;
    private String newDesignation;
    private String reason;
    private LocalDateTime effectiveDate;
    private LocalDateTime eventTimestamp;

    @JsonProperty("eventType")
    public String getEventType() {
        return "EmployeeTransferred";
    }

}
