package com.hrms.attendance_service.infrastructure.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendanceCheckOutEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    private String employeeId;
    private String attendanceId;
    private LocalDateTime checkOutTime;

    private Double workedHours;
    private Integer overtimeMinutes;

    @JsonProperty("eventType")
    public String getEventType() {
        return "AttendanceCheckOut";
    }
}
