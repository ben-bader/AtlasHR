package com.hrms.attendance_service.presentation.controller;

import com.hrms.attendance_service.application.dto.BulkAttendanceRequestDTO;
import com.hrms.attendance_service.application.service.BulkAttendanceService;
import com.hrms.attendance_service.common.api.ApiResponse;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
public class BulkAttendanceController {

    private final BulkAttendanceService bulkAttendanceService;

    @PostMapping("/bulk")
    public ApiResponse<String> uploadBulk(
            @RequestBody BulkAttendanceRequestDTO request
    ) {

        bulkAttendanceService.processBulk(request);

        return ApiResponse.success(
                "SUCCESS",
                "Bulk attendance processed successfully"
        );
    }
}
