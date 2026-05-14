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
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final VerificationEngineService verificationEngineService;
    private final JsonMapperUtil jsonMapperUtil;
    private final EventPublisherService eventPublisher;

    // =====================================================
    // CHECK IN (WITH ALL VERIFICATION METHODS)
    // =====================================================
    public Attendance checkIn(String employeeId,AttendanceVerificationRequestDTO request) {

        // VERIFY DEVICE + QR/NFC/BIOMETRIC/FACE
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
        attendance.setVerificationPayload(jsonMapperUtil.toJson(request));

        Shift shift = attendance.getShift();

        if (shift != null) {
            applyLateRule(attendance, shift, now.toLocalTime());
        } else {
            attendance.setStatus(AttendanceStatus.PRESENT);
        }

        Attendance saved = attendanceRepository.save(attendance);

        publishCreatedEvent(saved);

        return saved;
    }

    // =====================================================
    // CHECK OUT (WITH VERIFICATION ALSO)
    // =====================================================
    public Attendance checkOut(
            String employeeId,
            AttendanceVerificationRequestDTO request
    ) {

        // VERIFY  
        verificationEngineService.verify(request);

        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();

        Attendance attendance = attendanceRepository
                .findByEmployeeIdAndDate(employeeId, today)
                .orElseThrow(() ->
                        new ResourceNotFoundException("No attendance found")
                );

        if (attendance.getCheckOut() != null) {
            throw new BadRequestException("Already checked out today");
        }

        attendance.setCheckOut(now);

        if (attendance.getCheckIn() != null) {
            calculateWorkedHours(attendance);
            applyOvertimeRule(attendance);
        }

        Attendance saved = attendanceRepository.save(attendance);

        eventPublisher.publish(
                RabbitMQConfig.EXCHANGE_NAME,
                RabbitMQConfig.ATTENDANCE_CHECKOUT,
                AttendanceCheckOutEvent.builder()
                        .employeeId(employeeId)
                        .attendanceId(saved.getId().toString())
                        .checkOutTime(saved.getCheckOut())
                        .workedHours(saved.getWorkedHours())
                        .overtimeMinutes(saved.getOvertimeMinutes())
                        .build()
        );

        return saved;
    }

    // =====================================================
    // BULK ATTENDANCE
    // =====================================================
    public List<Attendance> createBulkAttendances(List<BulkAttendanceDTO> dtos) {

        List<Attendance> list = new ArrayList<>();

        for (BulkAttendanceDTO dto : dtos) {

            if (attendanceRepository
                    .existsByEmployeeIdAndDateAndDeletedFalse(
                            dto.getEmployeeId(),
                            dto.getDate()
                    )) {
                continue;
            }

            Attendance attendance = Attendance.builder()
                    .employeeId(dto.getEmployeeId())
                    .date(dto.getDate())
                    .checkIn(dto.getCheckIn())
                    .checkOut(dto.getCheckOut())
                    .status(AttendanceStatus.PRESENT)
                    .build();

            list.add(attendance);
        }

        return attendanceRepository.saveAll(list);
    }

    // =====================================================
    // DELETE
    // =====================================================
    public void deleteAttendance(Long id, String deletedBy) {

        Attendance attendance = attendanceRepository
                .findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Attendance not found"));

        attendance.setDeleted(true);
        attendanceRepository.save(attendance);

        publishDeletedEvent(attendance, deletedBy);
    }

    // =====================================================
    // RESTORE
    // =====================================================
    public void restoreAttendance(Long id) {

        Attendance attendance = attendanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Attendance not found"));

        attendance.setDeleted(false);
        attendanceRepository.save(attendance);
    }

    // =====================================================
    // GETTERS
    // =====================================================
    public Attendance getById(Long id) {
        return attendanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Attendance not found"));
    }

    public List<Attendance> getEmployeeAttendances(String employeeId) {
        return attendanceRepository.findByEmployeeId(employeeId);
    }

    public List<Attendance> getAttendancesByDate(String date) {
        return attendanceRepository.findByDate(LocalDate.parse(date));
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

    // =====================================================
    // BUSINESS LOGIC
    // =====================================================
    private void applyLateRule(Attendance attendance, Shift shift, LocalTime checkInTime) {

        LocalTime start = shift.getStartTime();
        int grace = shift.getGracePeriodMinutes();
        LocalTime allowedTime = start.plusMinutes(grace);

        if (checkInTime.isAfter(allowedTime)) {

            attendance.setStatus(AttendanceStatus.LATE);
            attendance.setIsLate(true);

            int minutesLate = (int) Duration.between(start, checkInTime).toMinutes();
            attendance.setLateMinutes(minutesLate);

        } else {
            attendance.setStatus(AttendanceStatus.PRESENT);
            attendance.setIsLate(false);
            attendance.setLateMinutes(0);
        }
    }

    private void calculateWorkedHours(Attendance attendance) {

        long minutes = Duration.between(
                attendance.getCheckIn(),
                attendance.getCheckOut()
        ).toMinutes();

        attendance.setWorkedHours(minutes / 60.0);
    }

    private void applyOvertimeRule(Attendance attendance) {

        Shift shift = attendance.getShift();

        if (shift == null || attendance.getWorkedHours() == null) return;

        double worked = attendance.getWorkedHours();
        double limit = shift.getMinimumWorkingHours();

        if (worked > limit) {
            attendance.setIsOvertime(true);
            attendance.setOvertimeMinutes((int) ((worked - limit) * 60));
        } else {
            attendance.setIsOvertime(false);
            attendance.setOvertimeMinutes(0);
        }
    }
}
