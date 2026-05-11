package com.hrms.attendance_service.presentation.controller;

import com.hrms.attendance_service.application.dto.ShiftPlanningRequestDTO;
import com.hrms.attendance_service.application.dto.ShiftPlanningResponseDTO;
import com.hrms.attendance_service.application.mapper.ShiftPlanningMapper;
import com.hrms.attendance_service.application.service.ShiftPlanningService;
import com.hrms.attendance_service.common.api.ApiResponse;
import com.hrms.attendance_service.common.enums.PlanningStatus;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shift-plannings")
@RequiredArgsConstructor
public class ShiftPlanningController {

    private final ShiftPlanningService service;
    private final ShiftPlanningMapper mapper;

    // ================= ASSIGN =================
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @PostMapping
    public ApiResponse<ShiftPlanningResponseDTO> assign(
            @Valid @RequestBody ShiftPlanningRequestDTO request
    ) {

        var saved = service.assignShift(
                mapper.toEntity(request)
        );

        return ApiResponse.success(
                mapper.toDto(saved),
                "Shift assigned successfully"
        );
    }

    // ================= EMPLOYEE =================
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @GetMapping("/employee/{employeeId}")
    public ApiResponse<List<ShiftPlanningResponseDTO>>
    getEmployeePlanning(
            @PathVariable String employeeId
    ) {

        var result = service.getEmployeePlanning(employeeId)
                .stream()
                .map(mapper::toDto)
                .toList();

        return ApiResponse.success(
                result,
                "Employee planning"
        );
    }

    // ================= MY ACTIVE =================
    @PreAuthorize("hasRole('EMPLOYEE')")
    @GetMapping("/employee/active")
    public ApiResponse<ShiftPlanningResponseDTO>
    getMyActivePlanning(
            Authentication authentication
    ) {

        String employeeId = authentication.getName();

        return ApiResponse.success(
                mapper.toDto(
                        service.getActivePlanning(employeeId)
                ),
                "My active planning"
        );
    }

    // ================= STATUS =================
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @GetMapping("/status/{status}")
    public ApiResponse<List<ShiftPlanningResponseDTO>>
    getByStatus(
            @PathVariable PlanningStatus status
    ) {

        var result = service.getByStatus(status)
                .stream()
                .map(mapper::toDto)
                .toList();

        return ApiResponse.success(
                result,
                "Planning by status"
        );
    }

    // ================= SHIFT =================
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @GetMapping("/shift/{shiftId}")
    public ApiResponse<List<ShiftPlanningResponseDTO>>
    getByShift(
            @PathVariable Long shiftId
    ) {

        var result = service.getByShift(shiftId)
                .stream()
                .map(mapper::toDto)
                .toList();

        return ApiResponse.success(
                result,
                "Shift planning"
        );
    }

    // ================= ACTIVE TODAY =================
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @GetMapping("/active-today")
    public ApiResponse<List<ShiftPlanningResponseDTO>>
    getActiveToday() {

        var result = service.getActiveToday()
                .stream()
                .map(mapper::toDto)
                .toList();

        return ApiResponse.success(
                result,
                "Active planning today"
        );
    }

    // ================= DEPARTMENT =================
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @GetMapping("/department/{departmentName}")
    public ApiResponse<List<ShiftPlanningResponseDTO>>
    getDepartmentPlanning(
            @PathVariable String departmentName
    ) {

        var result = service.getDepartmentPlanning(departmentName)
                .stream()
                .map(mapper::toDto)
                .toList();

        return ApiResponse.success(
                result,
                "Department planning"
        );
    }

    // ================= DELETE =================
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deletePlanning(
            @PathVariable Long id
    ) {

        service.deletePlanning(id);

        return ApiResponse.success(
                null,
                "Planning deleted"
        );
    }

    // ================= RESTORE =================
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/restore/{id}")
    public ApiResponse<Void> restorePlanning(
            @PathVariable Long id
    ) {

        service.restorePlanning(id);

        return ApiResponse.success(
                null,
                "Planning restored"
        );
    }

    // ================= COUNT ACTIVE =================
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @GetMapping("/count/active")
    public ApiResponse<Long> countActivePlanning() {

        return ApiResponse.success(
                service.countActivePlanning(),
                "Active planning count"
        );
    }
}
