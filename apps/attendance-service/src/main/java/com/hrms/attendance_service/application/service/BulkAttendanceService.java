package com.hrms.attendance_service.application.service;

import com.hrms.attendance_service.application.dto.*;
import com.hrms.common.exception.BadRequestException;
import com.hrms.attendance_service.domain.model.Attendance;
import com.hrms.attendance_service.domain.repository.AttendanceRepository;
import com.hrms.attendance_service.common.enums.AttendanceType;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BulkAttendanceService {

    private final AttendanceRepository attendanceRepository;

    public void processBulk(BulkAttendanceRequestDTO request) {

        if (request.getRecords() == null || request.getRecords().isEmpty()) {
            throw new BadRequestException("Empty bulk request");
        }

        List<Attendance> toSave = new ArrayList<>();

        for (BulkAttendanceRecordDTO record : request.getRecords()) {

            LocalDate date = record.getTimestamp().toLocalDate();

            boolean exists = attendanceRepository
                    .existsByEmployeeIdAndDateAndDeletedFalse(
                            record.getEmployeeId(),
                            date
                    );

            if (exists) continue;

            Attendance attendance = Attendance.builder()
                    .employeeId(record.getEmployeeId())
                    .date(date)
                    .checkIn(record.getType() == AttendanceType.IN
                            ? record.getTimestamp()
                            : null)
                    .checkOut(record.getType() == AttendanceType.OUT
                            ? record.getTimestamp()
                            : null)
                    .build();

            toSave.add(attendance);
        }

        attendanceRepository.saveAll(toSave);
    }
}
