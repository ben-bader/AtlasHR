package com.hrms.attendance_service.infrastructure.event;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendanceDeletedEvent implements Serializable {

    private String attendanceId;
    private String employeeId;
    private LocalDateTime deletedAt;
    private String deletedBy;

    @JsonProperty("eventType")
    public String getEventType() {
        return "AttendanceDeleted";
    }
}
