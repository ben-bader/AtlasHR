package com.hrms.attendance_service.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

import com.hrms.attendance_service.common.enums.PlanningStatus;

@Entity
@Table(name = "shift_plannings",
        uniqueConstraints = @UniqueConstraint(columnNames = {"employeeId","startDate","endDate"}))
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShiftPlanning extends BaseEntity {

    private String employeeId;

    private String employeeName;

    private String departmentName;

    private String designationName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shift_id")
    private Shift shift;

    private LocalDate startDate;

    private LocalDate endDate;

    private Boolean saturdayOff;

    private Boolean sundayOff;

    @Enumerated(EnumType.STRING)
    private PlanningStatus status;

    private String note;

    @PrePersist
    public void prePersist() {
        if (this.saturdayOff == null) this.saturdayOff = false;
        if (this.sundayOff == null) this.sundayOff = true;
        if (this.status == null) this.status = PlanningStatus.ACTIVE;
    }

    public boolean isActive() {
        return this.status == PlanningStatus.ACTIVE;
    }

    public boolean isInRange(LocalDate date) {
        return (date.isEqual(startDate) || date.isAfter(startDate)) &&
               (date.isEqual(endDate) || date.isBefore(endDate));
    }
}
