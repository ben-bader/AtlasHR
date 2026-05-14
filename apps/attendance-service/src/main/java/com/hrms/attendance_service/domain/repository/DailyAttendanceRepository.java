package com.hrms.attendance_service.domain.repository;

import com.hrms.attendance_service.domain.model.DailyAttendance;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DailyAttendanceRepository
        extends JpaRepository<DailyAttendance, Long> {

    // ================= DATE =================

    Optional<DailyAttendance>
    findByDateAndDeletedFalse(LocalDate date);

    // ================= RANGE =================

    List<DailyAttendance> findByDateBetweenAndDeletedFalse(
            LocalDate start,
            LocalDate end
    );

    // ================= PAGINATION =================

    Page<DailyAttendance>
    findAllByDeletedFalse(Pageable pageable);

    // ================= EXISTS =================

    boolean existsByDateAndDeletedFalse(
            LocalDate date
    );

    // ================= STATS =================

    long countByDeletedFalse();
}
