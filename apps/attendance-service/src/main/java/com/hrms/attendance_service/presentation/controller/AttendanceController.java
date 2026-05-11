package com.hrms.attendance_service.presentation.controller;

import com.hrms.attendance_service.application.dto.*;
import com.hrms.attendance_service.application.mapper.AttendanceMapper;
import com.hrms.attendance_service.application.service.AttendanceService;
import com.hrms.attendance_service.common.api.ApiResponse;
import com.hrms.attendance_service.common.utils.JsonMapperUtil;

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

    // ================= CHECK IN =================
    @PostMapping("/check-in")
    public ApiResponse<AttendanceResponseDTO> checkIn(
            @Valid @RequestBody CheckInRequestDTO request
    ) {

        var attendance = attendanceService.checkIn(
                request.getEmployeeId(),
                request.getMethod(),
                request.getVerificationPayload()
        );

        return ApiResponse.success(
                attendanceMapper.toResponse(attendance),
                "Check-in successful"
        );
    }

    // ================= CHECK OUT =================
    @PostMapping("/check-out")
    public ApiResponse<AttendanceResponseDTO> checkOut(
            @Valid @RequestBody CheckOutRequestDTO request
    ) {

        var attendance = attendanceService.checkOut(
                request.getEmployeeId()
        );

        return ApiResponse.success(
                attendanceMapper.toResponse(attendance),
                "Check-out successful"
        );
    }

    // ================= GET BY ID =================
    @GetMapping("/{id}")
    public ApiResponse<AttendanceResponseDTO> getById(@PathVariable Long id) {

        var attendance = attendanceService.getById(id);

        return ApiResponse.success(
                attendanceMapper.toResponse(attendance),
                "Attendance details"
        );
    }

    // ================= GET EMPLOYEE =================
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

    // ================= GET BY DATE =================
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

    // ================= DELETE (SOFT DELETE) =================
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteAttendance(
            @PathVariable Long id,
            Authentication authentication
    ) {

        attendanceService.deleteAttendance(id, authentication.getName());

        return ApiResponse.success(null, "Attendance deleted");
    }

    // ================= RESTORE =================
    @PutMapping("/restore/{id}")
    public ApiResponse<Void> restoreAttendance(@PathVariable Long id) {

        attendanceService.restoreAttendance(id);

        return ApiResponse.success(null, "Attendance restored");
    }
}
