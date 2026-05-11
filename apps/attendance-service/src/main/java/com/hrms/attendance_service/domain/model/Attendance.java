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

    private String note;

    @Enumerated(EnumType.STRING)
    private VerificationMethod method;

    private String deviceId;

    private String qrCodeUsed;

    private String nfcTagUsed;

    private Boolean verifiedByBiometric;

    // ================= COMPUTED =================
    private Boolean isLate;
    private Integer lateMinutes;

    private Boolean isOvertime;
    private Integer overtimeMinutes;

    @PrePersist
    public void prePersist() {

        if (this.date == null) this.date = LocalDate.now();
        if (this.status == null) this.status = AttendanceStatus.PRESENT;
        if (this.justified == null) this.justified = false;
        if (this.onLeave == null) this.onLeave = false;
        if (this.verifiedByBiometric == null) this.verifiedByBiometric = false;
        if (this.isLate == null) this.isLate = false;
        if (this.isOvertime == null) this.isOvertime = false;
    }

    public boolean isCheckedIn() {
        return checkIn != null;
    }

    public boolean isCheckedOut() {
        return checkOut != null;
    }
}
