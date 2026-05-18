package com.hrms.attendance_service.presentation.controller;

import com.hrms.attendance_service.application.dto.DailyAttendanceDTO;
import com.hrms.attendance_service.application.mapper.DailyAttendanceMapper;
import com.hrms.attendance_service.application.service.DailyAttendanceService;
import com.hrms.common.api.ApiResponse;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/daily-attendance")
@RequiredArgsConstructor
public class DailyAttendanceController {

    private final DailyAttendanceService service;
    private final DailyAttendanceMapper mapper;

    // ================= GET DATE =================
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @GetMapping("/date/{date}")
    public ApiResponse<DailyAttendanceDTO> getByDate(
            @PathVariable LocalDate date
    ) {

        return ApiResponse.success(
                mapper.toDto(
                        service.getByDate(date)
                ),
                "Daily attendance"
        );
    }

    // ================= RANGE =================
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @GetMapping("/range")
    public ApiResponse<List<DailyAttendanceDTO>> getRange(
            @RequestParam LocalDate start,
            @RequestParam LocalDate end
    ) {

        var result = service.getRange(start, end)
                .stream()
                .map(mapper::toDto)
                .toList();

        return ApiResponse.success(
                result,
                "Daily attendance range"
        );
    }

    // ================= PAGINATION =================
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @GetMapping
    public ApiResponse<Page<DailyAttendanceDTO>> getAll(
            Pageable pageable
    ) {

        Page<DailyAttendanceDTO> result =
                service.getAll(pageable)
                        .map(mapper::toDto);

        return ApiResponse.success(
                result,
                "Daily attendance list"
        );
    }

    // ================= DELETE =================
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(
            @PathVariable Long id
    ) {

        service.delete(id);

        return ApiResponse.success(
                null,
                "Daily attendance deleted"
        );
    }

    // ================= RESTORE =================
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/restore/{id}")
    public ApiResponse<Void> restore(
            @PathVariable Long id
    ) {

        service.restore(id);

        return ApiResponse.success(
                null,
                "Daily attendance restored"
        );
    }

    // ================= COUNT =================
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @GetMapping("/count")
    public ApiResponse<Long> count() {

        return ApiResponse.success(
                service.count(),
                "Total daily attendance"
        );
    }
}
