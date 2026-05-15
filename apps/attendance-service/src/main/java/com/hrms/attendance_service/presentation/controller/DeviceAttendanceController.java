package com.hrms.attendance_service.presentation.controller;

import com.hrms.attendance_service.application.dto.AttendanceVerificationRequestDTO;
import com.hrms.attendance_service.application.service.AttendanceService;
import com.hrms.attendance_service.application.service.VerificationEngineService;
import com.hrms.attendance_service.common.api.ApiResponse;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/device-attendance")
@RequiredArgsConstructor
public class DeviceAttendanceController {

    private final VerificationEngineService verificationEngineService;

    private final AttendanceService attendanceService;

    // VERIFY + CHECK-IN
    @PostMapping("/verify")
    public ApiResponse<String> verifyAttendance(

            @RequestBody
            AttendanceVerificationRequestDTO request
    ) {

        // 1. Verify biometrics / QR / NFC
        verificationEngineService.verify(request);

        // 3 check-in
        attendanceService.checkIn(request);

        return ApiResponse.success(
                "SUCCESS",
                "Attendance verified successfully"
        );
    }
}
