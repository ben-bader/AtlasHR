package com.hrms.attendance_service.application.service;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AttendanceHistoryService {

    private final AttendanceHistoryRepository historyRepository;

    public List<AttendanceHistory> getEmployeeHistory(String employeeId) {
        return historyRepository.findByEmployeeId(employeeId);
    }

    public List<AttendanceHistory> getAttendanceHistory(Long attendanceId) {
        return historyRepository.findByAttendanceId(attendanceId);
    }
}
