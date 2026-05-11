package com.hrms.attendance_service.domain.repository;

import com.hrms.attendance_service.domain.model.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    Optional<Attendance> findByEmployeeIdAndDate(String employeeId, LocalDate date);

    List<Attendance> findByEmployeeId(String employeeId);

    List<Attendance> findByDate(LocalDate date);

    Optional<Attendance> findByIdAndDeletedFalse(Long id);

    boolean existsByEmployeeIdAndDateAndDeletedFalse(
            String employeeId,
            LocalDate date
    );
}
