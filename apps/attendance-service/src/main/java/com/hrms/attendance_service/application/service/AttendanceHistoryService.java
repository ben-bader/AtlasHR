package com.hrms.attendance_service.application.service;

import com.hrms.attendance_service.common.enums.AttendanceAction;
import com.hrms.attendance_service.common.exceptions.ResourceNotFoundException;
import com.hrms.attendance_service.domain.model.AttendanceHistory;
import com.hrms.attendance_service.domain.repository.AttendanceHistoryRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AttendanceHistoryService {

    private final AttendanceHistoryRepository historyRepository;

    // ================= EMPLOYEE =================
    public Page<AttendanceHistory> getEmployeeHistory(
            String employeeId,
            Pageable pageable
    ) {

        return historyRepository
                .findByEmployeeIdAndDeletedFalse(
                        employeeId,
                        pageable
                );
    }

    // ================= ATTENDANCE =================
    public List<AttendanceHistory> getAttendanceHistory(
            Long attendanceId
    ) {

        return historyRepository
                .findByAttendanceIdAndDeletedFalse(
                        attendanceId
                );
    }

    // ================= ACTION =================
    public List<AttendanceHistory> getByAction(
            AttendanceAction action
    ) {

        return historyRepository
                .findByActionAndDeletedFalse(action);
    }

    // ================= RANGE =================
    public List<AttendanceHistory> getRange(
            LocalDateTime start,
            LocalDateTime end
    ) {

        return historyRepository
                .findByActionTimeBetweenAndDeletedFalse(
                        start,
                        end
                );
    }

    // ================= USER =================
    public List<AttendanceHistory> getByPerformedBy(
            String performedBy
    ) {

        return historyRepository
                .findByPerformedByAndDeletedFalse(
                        performedBy
                );
    }

    // ================= DELETE =================
    public void delete(Long id) {

        AttendanceHistory history =
                historyRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "History not found"
                                )
                        );

        history.setDeleted(true);

        historyRepository.save(history);
    }

    // ================= COUNT =================
    public long count() {
        return historyRepository.countByDeletedFalse();
    }
}
