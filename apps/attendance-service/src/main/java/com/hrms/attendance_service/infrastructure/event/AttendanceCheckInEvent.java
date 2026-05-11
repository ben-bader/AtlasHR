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
public class AttendanceCheckInEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    private String employeeId;
    private String attendanceId;
    private LocalDateTime checkInTime;

    private String method; // QR_CODE / NFC / FACE / FINGERPRINT

    @JsonProperty("eventType")
    public String getEventType() {
        return "AttendanceCheckIn";
    }
}
