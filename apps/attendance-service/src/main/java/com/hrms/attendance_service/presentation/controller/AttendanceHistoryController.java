package com.hrms.attendance_service.presentation.controller;

import com.hrms.attendance_service.application.dto.AttendanceHistoryDTO;
import com.hrms.attendance_service.application.mapper.AttendanceHistoryMapper;
import com.hrms.attendance_service.application.service.AttendanceHistoryService;
import com.hrms.common.api.ApiResponse;
import com.hrms.attendance_service.common.enums.AttendanceAction;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/attendance-history")
@RequiredArgsConstructor
public class AttendanceHistoryController {

        private final AttendanceHistoryService service;
        private final AttendanceHistoryMapper mapper;

        // ================= EMPLOYEE =================
        @PreAuthorize("hasAnyRole('ADMIN','HR')")
        @GetMapping("/employee/{employeeId}")
        public ApiResponse<Page<AttendanceHistoryDTO>> getEmployeeHistory(
                        @PathVariable String employeeId,
                        Pageable pageable) {

                Page<AttendanceHistoryDTO> result = service.getEmployeeHistory(
                                employeeId,
                                pageable)
                                .map(mapper::toDto);

                return ApiResponse.success(
                                result,
                                "Employee history");
        }

        // ================= ATTENDANCE =================
        @PreAuthorize("hasAnyRole('ADMIN','HR')")
        @GetMapping("/attendance/{attendanceId}")
        public ApiResponse<List<AttendanceHistoryDTO>> getAttendanceHistory(
                        @PathVariable Long attendanceId) {

                var result = service.getAttendanceHistory(attendanceId)
                                .stream()
                                .map(mapper::toDto)
                                .toList();

                return ApiResponse.success(
                                result,
                                "Attendance history");
        }

        // ================= ACTION =================
        @PreAuthorize("hasAnyRole('ADMIN','HR')")
        @GetMapping("/action/{action}")
        public ApiResponse<List<AttendanceHistoryDTO>> getByAction(
                        @PathVariable AttendanceAction action) {

                var result = service.getByAction(action)
                                .stream()
                                .map(mapper::toDto)
                                .toList();

                return ApiResponse.success(
                                result,
                                "History by action");
        }

        // ================= RANGE =================
        @PreAuthorize("hasAnyRole('ADMIN','HR')")
        @GetMapping("/range")
        public ApiResponse<List<AttendanceHistoryDTO>> getRange(
                        @RequestParam LocalDateTime start,
                        @RequestParam LocalDateTime end) {

                var result = service.getRange(start, end)
                                .stream()
                                .map(mapper::toDto)
                                .toList();

                return ApiResponse.success(
                                result,
                                "History range");
        }

        // ================= USER =================
        @PreAuthorize("hasRole('ADMIN')")
        @GetMapping("/performed-by/{performedBy}")
        public ApiResponse<List<AttendanceHistoryDTO>> getByPerformedBy(
                        @PathVariable String performedBy) {

                var result = service.getByPerformedBy(performedBy)
                                .stream()
                                .map(mapper::toDto)
                                .toList();

                return ApiResponse.success(
                                result,
                                "History by performer");
        }

        // ================= DELETE =================
        @PreAuthorize("hasRole('ADMIN')")
        @DeleteMapping("/{id}")
        public ApiResponse<Void> delete(
                        @PathVariable Long id) {

                service.delete(id);

                return ApiResponse.success(
                                null,
                                "History deleted");
        }

        // ================= COUNT =================
        @PreAuthorize("hasAnyRole('ADMIN','HR')")
        @GetMapping("/count")
        public ApiResponse<Long> count() {

                return ApiResponse.success(
                                service.count(),
                                "Total history count");
        }
}
