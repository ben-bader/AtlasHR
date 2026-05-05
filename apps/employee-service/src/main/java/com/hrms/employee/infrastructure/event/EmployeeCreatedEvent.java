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
public class EmployeeCreatedEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    private String employeeId;
    private String firstName;
    private String lastName;
    private String email;
    private String departmentId;
    private String designationId;
    private String joiningDate;
    private LocalDateTime eventTimestamp;

    @JsonProperty("eventType")
    public String getEventType() {
        return "EmployeeCreated";
    }

}
