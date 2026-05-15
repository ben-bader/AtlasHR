package com.hrms.attendance_service.presentation.controller;

import com.hrms.attendance_service.application.dto.AttendanceVerificationRequestDTO;
import com.hrms.attendance_service.application.service.AttendanceService;
import com.hrms.attendance_service.application.service.DeviceAuthService;
import com.hrms.attendance_service.application.service.VerificationEngineService;
import com.hrms.attendance_service.common.api.ApiResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/device-attendance")
@RequiredArgsConstructor
public class DeviceAttendanceController {

    private final DeviceAuthService deviceAuthService;
    private final VerificationEngineService verificationService;
    private final AttendanceService attendanceService;

    @PostMapping("/verify")
    public ApiResponse<String> verifyAttendance(

            @RequestHeader("X-API-KEY")
            String apiKey,

            @RequestBody
            AttendanceVerificationRequestDTO request
    ) {

        // 1 validate device
        deviceAuthService.validateDeviceKey(apiKey);

        // 2 verify method
        verificationService.verify(request);

        // 3 check-in

        attendanceService.checkIn(request.getEmployeeId(), request);

        return ApiResponse.success(
                "SUCCESS",
                "Attendance verified"
        );
    }
}
