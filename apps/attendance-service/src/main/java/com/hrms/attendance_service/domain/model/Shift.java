package com.hrms.attendance_service.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import com.hrms.attendance_service.common.enums.ShiftType;
import com.hrms.attendance_service.common.enums.VerificationMethod;
import com.hrms.attendance_service.common.enums.WorkDay;

@Entity
@Table(
        name = "shifts",
        uniqueConstraints = @UniqueConstraint(columnNames = "name")
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Shift extends BaseEntity {

    // ================= BASIC INFO =================

    @Column(nullable = false, unique = true)
    private String name;

    private String description;

    @Enumerated(EnumType.STRING)
    private ShiftType shiftType;

    // ================= TIME =================

    @Column(nullable = false)
    private LocalTime startTime;

    @Column(nullable = false)
    private LocalTime endTime;

    // ================= RULES =================

    // delay tolerance (minutes)
    private Integer gracePeriodMinutes;

    // minimum required hours per day
    private Double minimumWorkingHours;

    // overtime starts after this
    private Double overtimeAfterHours;

    private Boolean active;

    // ================= WORKING DAYS =================

    @ElementCollection
    @Enumerated(EnumType.STRING)
    @CollectionTable(
            name = "shift_working_days",
            joinColumns = @JoinColumn(name = "shift_id")
    )
    @Builder.Default
    private List<WorkDay> workingDays = new ArrayList<>();

    // ================= VERIFICATION METHODS =================

    @ElementCollection
    @Enumerated(EnumType.STRING)
    @CollectionTable(
            name = "shift_verification_methods",
            joinColumns = @JoinColumn(name = "shift_id")
    )
    @Builder.Default
    private List<VerificationMethod> verificationMethods = new ArrayList<>();

    // ================= RELATIONS =================

    @OneToMany(mappedBy = "shift", fetch = FetchType.LAZY)
    private List<ShiftPlanning> plannings;

    @OneToMany(mappedBy = "shift", fetch = FetchType.LAZY)
    private List<Attendance> attendances;

    // ================= BUSINESS LOGIC =================

    @PrePersist
    public void prePersist() {

        if (this.gracePeriodMinutes == null)
            this.gracePeriodMinutes = 15;

        if (this.minimumWorkingHours == null)
            this.minimumWorkingHours = 8.0;

        if (this.overtimeAfterHours == null)
            this.overtimeAfterHours = 8.0;

        if (this.active == null)
            this.active = true;
    }

    // ================= HELPERS =================

    public boolean isNightShift() {
        return endTime.isBefore(startTime);
    }

    public boolean isActive() {
        return Boolean.TRUE.equals(active);
    }

    public boolean supportsMethod(VerificationMethod method) {
        return verificationMethods != null && verificationMethods.contains(method);
    }

    public boolean worksOnDay(WorkDay day) {
        return workingDays != null && workingDays.contains(day);
    }
}
