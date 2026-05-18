package com.hrms.attendance_service.presentation.controller;

import com.hrms.attendance_service.application.dto.*;
import com.hrms.attendance_service.application.mapper.AttendanceMapper;
import com.hrms.attendance_service.application.service.AttendanceService;
import com.hrms.common.api.ApiResponse;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/attendances")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;
    private final AttendanceMapper attendanceMapper;

    // =====================================================
    // CHECK IN (QR / NFC / FACE / FINGERPRINT)
    // =====================================================
    @PreAuthorize("hasRole('EMPLOYEE')")
    @PostMapping("/check-in")
    public ApiResponse<AttendanceResponseDTO> checkIn(@Valid @RequestBody AttendanceVerificationRequestDTO request,Authentication authentication) {

        String employeeId = authentication.getName();

        var attendance = attendanceService.checkIn(
                employeeId,
                request
        );

        return ApiResponse.success(
                attendanceMapper.toResponse(attendance),
                "Check-in successful"
        );
    }

    // =====================================================
    // CHECK OUT (WITH VERIFICATION ALSO)
    // =====================================================
    @PreAuthorize("hasRole('EMPLOYEE')")
    @PostMapping("/check-out")
    public ApiResponse<AttendanceResponseDTO> checkOut(
            @Valid @RequestBody AttendanceVerificationRequestDTO request,
            Authentication authentication
    ) {

        String employeeId = authentication.getName();

        var attendance = attendanceService.checkOut(
                employeeId,
                request
        );

        return ApiResponse.success(
                attendanceMapper.toResponse(attendance),
                "Check-out successful"
        );
    }

    // =====================================================
    // GET EMPLOYEE ATTENDANCES (ADMIN/HR)
    // =====================================================
    @PreAuthorize("hasAnyRole('HR','ADMIN')")
    @GetMapping("/employee/{employeeId}")
    public ApiResponse<List<AttendanceResponseDTO>> getEmployeeAttendances(
            @PathVariable String employeeId
    ) {

        var result = attendanceService.getEmployeeAttendances(employeeId)
                .stream()
                .map(attendanceMapper::toResponse)
                .toList();

        return ApiResponse.success(result, "Employee attendances");
    }

    // =====================================================
    // MY ATTENDANCE
    // =====================================================
    @PreAuthorize("hasRole('EMPLOYEE')")
    @GetMapping("/me")
    public ApiResponse<List<AttendanceResponseDTO>> getMyAttendances(
            Authentication authentication
    ) {

        String employeeId = authentication.getName();

        var result = attendanceService.getEmployeeAttendances(employeeId)
                .stream()
                .map(attendanceMapper::toResponse)
                .toList();

        return ApiResponse.success(result, "My attendances");
    }

    // =====================================================
    // GET BY DATE
    // =====================================================
    @PreAuthorize("hasAnyRole('HR','ADMIN')")
    @GetMapping("/date/{date}")
    public ApiResponse<List<AttendanceResponseDTO>> getByDate(
            @PathVariable String date
    ) {

        var result = attendanceService.getAttendancesByDate(date)
                .stream()
                .map(attendanceMapper::toResponse)
                .toList();

        return ApiResponse.success(result, "Attendances by date");
    }

    // =====================================================
    // GET BY ID
    // =====================================================
    @PreAuthorize("hasAnyRole('HR','ADMIN')")
    @GetMapping("/{id}")
    public ApiResponse<AttendanceResponseDTO> getById(@PathVariable Long id) {

        var attendance = attendanceService.getById(id);

        return ApiResponse.success(
                attendanceMapper.toResponse(attendance),
                "Attendance details"
        );
    }

    // =====================================================
    // DELETE (SOFT DELETE)
    // =====================================================
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteAttendance(
            @PathVariable Long id,
            Authentication authentication
    ) {

        attendanceService.deleteAttendance(id, authentication.getName());

        return ApiResponse.success(null, "Attendance deleted");
    }

    // =====================================================
    // RESTORE
    // =====================================================
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/restore/{id}")
    public ApiResponse<Void> restoreAttendance(@PathVariable Long id) {

        attendanceService.restoreAttendance(id);

        return ApiResponse.success(null, "Attendance restored");
    }
    
}
