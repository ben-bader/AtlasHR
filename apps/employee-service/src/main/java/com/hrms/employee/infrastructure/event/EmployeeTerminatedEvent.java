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
public class EmployeeTerminatedEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    private String employeeId;
    private String employeeName;
    private String email;
    private String departmentId;
    private String designationId;
    private String lastWorkingDate;
    private String terminationReason;
    private LocalDateTime eventTimestamp;

    @JsonProperty("eventType")
    public String getEventType() {
        return "EmployeeTerminated";
    }

}
