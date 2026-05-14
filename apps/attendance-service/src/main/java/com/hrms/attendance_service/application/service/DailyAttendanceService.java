package com.hrms.attendance_service.application.service;

import com.hrms.attendance_service.common.exceptions.BadRequestException;
import com.hrms.attendance_service.common.exceptions.ResourceNotFoundException;
import com.hrms.attendance_service.domain.model.DailyAttendance;
import com.hrms.attendance_service.domain.repository.DailyAttendanceRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DailyAttendanceService {

    private final DailyAttendanceRepository repository;

    // ================= CREATE =================
    public DailyAttendance create(
            DailyAttendance dailyAttendance
    ) {

        boolean exists =
                repository.existsByDateAndDeletedFalse(
                        dailyAttendance.getDate()
                );

        if (exists) {
            throw new BadRequestException(
                    "Daily attendance already exists"
            );
        }

        dailyAttendance.calculateStats();

        return repository.save(dailyAttendance);
    }

    // ================= GET BY DATE =================
    public DailyAttendance getByDate(
            LocalDate date
    ) {

        return repository
                .findByDateAndDeletedFalse(date)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Daily attendance not found"
                        )
                );
    }

    // ================= GET RANGE =================
    public List<DailyAttendance> getRange(
            LocalDate start,
            LocalDate end
    ) {

        return repository
                .findByDateBetweenAndDeletedFalse(
                        start,
                        end
                );
    }

    // ================= PAGINATION =================
    public Page<DailyAttendance> getAll(
            Pageable pageable
    ) {

        return repository
                .findAllByDeletedFalse(pageable);
    }

    // ================= DELETE =================
    public void delete(Long id) {

        DailyAttendance attendance =
                repository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Daily attendance not found"
                                )
                        );

        attendance.setDeleted(true);

        repository.save(attendance);
    }

    // ================= RESTORE =================
    public void restore(Long id) {

        DailyAttendance attendance =
                repository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Daily attendance not found"
                                )
                        );

        attendance.setDeleted(false);

        repository.save(attendance);
    }

    // ================= COUNT =================
    public long count() {
        return repository.countByDeletedFalse();
    }
}
