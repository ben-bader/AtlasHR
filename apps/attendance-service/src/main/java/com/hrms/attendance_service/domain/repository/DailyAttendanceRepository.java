package com.hrms.attendance_service.domain.repository;

import com.hrms.attendance_service.domain.model.DailyAttendance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface DailyAttendanceRepository extends JpaRepository<DailyAttendance, Long> {

    Optional<DailyAttendance> findByDate(LocalDate date);
}

