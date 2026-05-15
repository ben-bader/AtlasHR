package com.hrms.attendance_service.application.service;

import com.hrms.attendance_service.application.dto.AttendanceVerificationRequestDTO;
import com.hrms.attendance_service.application.dto.BulkAttendanceDTO;
import com.hrms.attendance_service.common.enums.AttendanceStatus;
import com.hrms.attendance_service.common.exceptions.BadRequestException;
import com.hrms.attendance_service.common.exceptions.ResourceNotFoundException;
import com.hrms.attendance_service.common.utils.JsonMapperUtil;
import com.hrms.attendance_service.domain.model.Attendance;
import com.hrms.attendance_service.domain.model.Shift;
import com.hrms.attendance_service.domain.repository.AttendanceRepository;
import com.hrms.attendance_service.infrastructure.event.*;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final VerificationEngineService verificationEngineService;
    private final JsonMapperUtil jsonMapperUtil;
    private final EventPublisherService eventPublisher;

    // =====================================================
    // CHECK IN
    // =====================================================
    public Attendance checkIn(String employeeId,
                              AttendanceVerificationRequestDTO request) {

        verificationEngineService.verify(request);

        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();

        Attendance attendance = attendanceRepository
                .findByEmployeeIdAndDate(employeeId, today)
                .orElse(Attendance.builder()
                        .employeeId(employeeId)
                        .date(today)
                        .build());

        if (attendance.getCheckIn() != null) {
            throw new BadRequestException("Already checked in today");
        }

        attendance.setCheckIn(now);
        attendance.setMethod(request.getMethod());

        // SAFE payload
        attendance.setVerificationPayload(
                jsonMapperUtil.toJson(
                        Map.of(
                                "method", request.getMethod().name(),
                                "deviceUid", request.getDeviceUid()
                        )
                )
        );

        applyShiftRulesOnCheckIn(attendance, now);

        Attendance saved = attendanceRepository.save(attendance);

        publishCreatedEvent(saved);

        return saved;
    }

    // =====================================================
    // CHECK OUT
    // =====================================================
    public Attendance checkOut(String employeeId,
                               AttendanceVerificationRequestDTO request) {

        verificationEngineService.verify(request);

        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();

        Attendance attendance = attendanceRepository
                .findByEmployeeIdAndDate(employeeId, today)
                .orElseThrow(() ->
                        new ResourceNotFoundException("No attendance found"));

        if (attendance.getCheckOut() != null) {
            throw new BadRequestException("Already checked out today");
        }

        attendance.setCheckOut(now);

        applyShiftRulesOnCheckOut(attendance);

        Attendance saved = attendanceRepository.save(attendance);

        publishCheckOutEvent(saved);

        return saved;
    }

    // =====================================================
    // SHIFT RULES
    // =====================================================
    private void applyShiftRulesOnCheckIn(Attendance attendance,
                                          LocalDateTime checkInTime) {

        Shift shift = attendance.getShift();

        if (shift == null) {
            attendance.setStatus(AttendanceStatus.PRESENT);
            return;
        }

        LocalTime start = shift.getStartTime();
        LocalTime allowed = start.plusMinutes(shift.getGracePeriodMinutes());

        LocalTime checkIn = checkInTime.toLocalTime();

        if (checkIn.isAfter(allowed)) {

            attendance.setStatus(AttendanceStatus.LATE);
            attendance.setIsLate(true);

            int lateMinutes =
                    (int) Duration.between(start, checkIn).toMinutes();

            attendance.setLateMinutes(lateMinutes);

        } else {
            attendance.setStatus(AttendanceStatus.PRESENT);
            attendance.setIsLate(false);
            attendance.setLateMinutes(0);
        }
    }

    // =====================================================
    private void applyShiftRulesOnCheckOut(Attendance attendance) {

        if (attendance.getCheckIn() == null ||
            attendance.getCheckOut() == null) return;

        long minutes = Duration.between(
                attendance.getCheckIn(),
                attendance.getCheckOut()
        ).toMinutes();

        attendance.setWorkedHours(minutes / 60.0);

        Shift shift = attendance.getShift();

        if (shift == null) return;

        double workedMinutes = minutes;
        double limitMinutes = shift.getMinimumWorkingHours() * 60;

        if (workedMinutes > limitMinutes) {

            attendance.setIsOvertime(true);
            attendance.setOvertimeMinutes(
                    (int) (workedMinutes - limitMinutes)
            );

        } else {
            attendance.setIsOvertime(false);
            attendance.setOvertimeMinutes(0);
        }
    }

    // =====================================================
    // EVENTS
    // =====================================================
    private void publishCreatedEvent(Attendance attendance) {

        AttendanceCreatedEvent event = AttendanceCreatedEvent.builder()
                .attendanceId(attendance.getId().toString())
                .employeeId(attendance.getEmployeeId())
                .date(attendance.getDate())
                .status(attendance.getStatus().name())
                .build();

        eventPublisher.publish(
                RabbitMQConfig.EXCHANGE_NAME,
                RabbitMQConfig.ATTENDANCE_CREATED,
                event
        );
    }

    private void publishCheckOutEvent(Attendance attendance) {

        AttendanceCheckOutEvent event = AttendanceCheckOutEvent.builder()
                .attendanceId(attendance.getId().toString())
                .employeeId(attendance.getEmployeeId())
                .checkOutTime(attendance.getCheckOut())
                .workedHours(attendance.getWorkedHours())
                .overtimeMinutes(attendance.getOvertimeMinutes())
                .build();

        eventPublisher.publish(
                RabbitMQConfig.EXCHANGE_NAME,
                RabbitMQConfig.ATTENDANCE_CHECKOUT,
                event
        );
    }

    // =====================================================
    // GETTERS
    // =====================================================
    public Attendance getById(Long id) {
        return attendanceRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Attendance not found"));
    }

    public List<Attendance> getEmployeeAttendances(String employeeId) {
        return attendanceRepository.findByEmployeeId(employeeId);
    }

    public List<Attendance> getAttendancesByDate(String date) {
        return attendanceRepository.findByDate(LocalDate.parse(date));
    }

    // =====================================================
    // DELETE / RESTORE
    // =====================================================
    public void deleteAttendance(Long id, String deletedBy) {

        Attendance attendance = attendanceRepository
                .findByIdAndDeletedFalse(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Attendance not found"));

        attendance.setDeleted(true);
        attendanceRepository.save(attendance);

        publishDeletedEvent(attendance, deletedBy);
    }

    public void restoreAttendance(Long id) {

        Attendance attendance = attendanceRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Attendance not found"));

        attendance.setDeleted(false);
        attendanceRepository.save(attendance);
    }

    private void publishDeletedEvent(Attendance attendance, String deletedBy) {

        AttendanceDeletedEvent event = AttendanceDeletedEvent.builder()
                .attendanceId(attendance.getId().toString())
                .employeeId(attendance.getEmployeeId())
                .deletedAt(LocalDateTime.now())
                .deletedBy(deletedBy)
                .build();

        eventPublisher.publish(
                RabbitMQConfig.EXCHANGE_NAME,
                RabbitMQConfig.ATTENDANCE_DELETED,
                event
        );
    }
}
