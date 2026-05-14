package com.hrms.attendance_service.domain.repository;

import com.hrms.attendance_service.common.enums.AttendanceAction;
import com.hrms.attendance_service.domain.model.AttendanceHistory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AttendanceHistoryRepository
        extends JpaRepository<AttendanceHistory, Long> {

    // ================= EMPLOYEE =================

    List<AttendanceHistory>
    findByEmployeeIdAndDeletedFalse(
            String employeeId
    );

    Page<AttendanceHistory>
    findByEmployeeIdAndDeletedFalse(
            String employeeId,
            Pageable pageable
    );

    // ================= ATTENDANCE =================

    List<AttendanceHistory>
    findByAttendanceIdAndDeletedFalse(
            Long attendanceId
    );

    // ================= ACTION =================

    List<AttendanceHistory>
    findByActionAndDeletedFalse(
            AttendanceAction action
    );

    // ================= DATE RANGE =================

    List<AttendanceHistory>
    findByActionTimeBetweenAndDeletedFalse(
            LocalDateTime start,
            LocalDateTime end
    );

    // ================= USER =================

    List<AttendanceHistory>
    findByPerformedByAndDeletedFalse(
            String performedBy
    );

    // ================= COUNT =================

    long countByDeletedFalse();
}
