package com.hrms.attendance_service.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

import com.hrms.attendance_service.common.enums.AttendanceAction;
import com.hrms.attendance_service.common.enums.VerificationMethod;

@Entity
@Table(name = "attendance_history")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceHistory extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attendance_id")
    private Attendance attendance;

    private String employeeId;

    private LocalDateTime actionTime;

    @Enumerated(EnumType.STRING)
    private AttendanceAction action;

    @Enumerated(EnumType.STRING)
    private VerificationMethod method;

    private String description;

    private String oldValue;

    private String newValue;

    private String performedBy;

    private String deviceInfo;

    @PrePersist
    public void prePersist() {
        if (this.actionTime == null)
            this.actionTime = LocalDateTime.now();

        if (this.performedBy == null)
            this.performedBy = "SYSTEM";
    }
}
