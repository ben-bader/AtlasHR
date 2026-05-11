package com.hrms.attendance_service.infrastructure.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendanceCreatedEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    private String attendanceId;
    private String employeeId;
    private LocalDate date;

    private String status;

    @JsonProperty("eventType")
    public String getEventType() {
        return "AttendanceCreated";
    }
}
