package com.hrms.attendance_service.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.hrms.attendance_service.common.enums.AttendanceStatus;
import com.hrms.attendance_service.common.enums.VerificationMethod;

@Entity
@Table(name = "attendances",
        uniqueConstraints = @UniqueConstraint(columnNames = {"employeeId","date"}))
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Attendance extends BaseEntity {

    private String employeeId;

    private String departmentName;
    private String designationName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shift_id")
    private Shift shift;

    private LocalDate date;

    private LocalDateTime checkIn;
    private LocalDateTime checkOut;

    @Enumerated(EnumType.STRING)
    private AttendanceStatus status;

    private Double workedHours;

    private Boolean justified;
    private Boolean onLeave;

    @Column(length = 2000)
    private String note;

    @Enumerated(EnumType.STRING)
    private VerificationMethod method;

    
    @Column(columnDefinition = "TEXT")
    private String verificationPayload;

    // ================= COMPUTED (NOT DB FIELDS) =================
    @Transient
    private Boolean isLate;

    @Transient
    private Integer lateMinutes;

    @Transient
    private Boolean isOvertime;

    @Transient
    private Integer overtimeMinutes;

    @PrePersist
    public void prePersist() {
        if (this.date == null) this.date = LocalDate.now();
        if (this.status == null) this.status = AttendanceStatus.PRESENT;
        if (this.justified == null) this.justified = false;
        if (this.onLeave == null) this.onLeave = false;
    }

    public boolean isCheckedIn() {
        return checkIn != null;
    }

    public boolean isCheckedOut() {
        return checkOut != null;
    }
}
