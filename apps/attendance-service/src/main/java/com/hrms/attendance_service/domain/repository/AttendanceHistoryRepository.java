package com.hrms.attendance_service.domain.repository;

import com.hrms.attendance_service.domain.model.AttendanceHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AttendanceHistoryRepository extends JpaRepository<AttendanceHistory, Long> {

    List<AttendanceHistory> findByEmployeeId(String employeeId);

    List<AttendanceHistory> findByAttendanceId(Long attendanceId);
}
