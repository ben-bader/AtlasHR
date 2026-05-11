package com.hrms.attendance_service.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "daily_attendance",
        uniqueConstraints = @UniqueConstraint(columnNames = "date"))
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailyAttendance extends BaseEntity {

    private LocalDate date;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "daily_attendance_id")
    private List<Attendance> attendances = new ArrayList<>();

    private Integer totalPresent;

    private Integer totalLate;

    private Integer totalAbsent;

    private Integer totalOnLeave;

    public void calculateStats() {

        this.totalPresent = (int) attendances.stream()
                .filter(a -> a.getStatus().name().equals("PRESENT"))
                .count();

        this.totalLate = (int) attendances.stream()
                .filter(a -> Boolean.TRUE.equals(a.getIsLate()))
                .count();

        this.totalAbsent = (int) attendances.stream()
                .filter(a -> a.getStatus().name().equals("ABSENT"))
                .count();

        this.totalOnLeave = (int) attendances.stream()
                .filter(a -> Boolean.TRUE.equals(a.getOnLeave()))
                .count();
    }
}
