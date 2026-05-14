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

    // =====================================================
    // ASSIGN SHIFT
    // =====================================================

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

    // =====================================================
    // EMPLOYEE FULL PLANNING
    // =====================================================

    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @GetMapping("/employee/{employeeId}")
    public ApiResponse<List<ShiftPlanningResponseDTO>> getEmployeePlanning(
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

    // =====================================================
    // MY ACTIVE PLANNING
    // =====================================================

    @PreAuthorize("hasRole('EMPLOYEE')")
    @GetMapping("/employee/active")
    public ApiResponse<ShiftPlanningResponseDTO> getMyActivePlanning(
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

    // =====================================================
    // LATEST PLANNING 
    // =====================================================

    @PreAuthorize("hasAnyRole('EMPLOYEE','HR','ADMIN')")
    @GetMapping("/employee/latest")
    public ApiResponse<ShiftPlanningResponseDTO> getLatest(
            Authentication authentication
    ) {

        String employeeId = authentication.getName();

        return ApiResponse.success(
                mapper.toDto(
                        service.getLatestPlanning(employeeId)
                ),
                "Latest planning"
        );
    }

    // =====================================================
    // STATUS FILTER
    // =====================================================

    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @GetMapping("/status/{status}")
    public ApiResponse<List<ShiftPlanningResponseDTO>> getByStatus(
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

    // =====================================================
    // TEAM ROSTER
    // =====================================================

    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @GetMapping("/team/{teamName}")
    public ApiResponse<List<ShiftPlanningResponseDTO>> getByTeam(
            @PathVariable String teamName
    ) {

        var result = service.getTeamPlanning(teamName)
                .stream()
                .map(mapper::toDto)
                .toList();

        return ApiResponse.success(
                result,
                "Team planning"
        );
    }

    // =====================================================
    // DEPARTMENT ROSTER
    // =====================================================

    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @GetMapping("/department/{departmentName}")
    public ApiResponse<List<ShiftPlanningResponseDTO>> getDepartment(
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

    // =====================================================
    // DATE RANGE (ROSTER CALENDAR)
    // =====================================================

    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @GetMapping("/range")
    public ApiResponse<List<ShiftPlanningResponseDTO>> getByDateRange(
            @RequestParam String start,
            @RequestParam String end
    ) {

        var result = service.getByDateRange(
                        java.time.LocalDate.parse(start),
                        java.time.LocalDate.parse(end)
                )
                .stream()
                .map(mapper::toDto)
                .toList();

        return ApiResponse.success(
                result,
                "Planning by date range"
        );
    }

    // =====================================================
    // TODAY ROSTER
    // =====================================================

    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @GetMapping("/today")
    public ApiResponse<List<ShiftPlanningResponseDTO>> getTodayRoster() {

        var result = service.getTodayRoster()
                .stream()
                .map(mapper::toDto)
                .toList();

        return ApiResponse.success(
                result,
                "Today's roster"
        );
    }

    // =====================================================
    // FLEXIBLE SHIFTS
    // =====================================================

    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @GetMapping("/flexible")
    public ApiResponse<List<ShiftPlanningResponseDTO>> getFlexible() {

        var result = service.getFlexibleShifts()
                .stream()
                .map(mapper::toDto)
                .toList();

        return ApiResponse.success(
                result,
                "Flexible shifts"
        );
    }

    // =====================================================
    // AUTO GENERATED
    // =====================================================

    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @GetMapping("/auto-generated")
    public ApiResponse<List<ShiftPlanningResponseDTO>> getAutoGenerated() {

        var result = service.getAutoGenerated()
                .stream()
                .map(mapper::toDto)
                .toList();

        return ApiResponse.success(
                result,
                "Auto generated planning"
        );
    }

    // =====================================================
    // DELETE
    // =====================================================

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {

        service.delete(id);

        return ApiResponse.success(null, "Planning deleted");
    }

    // =====================================================
    // RESTORE
    // =====================================================

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/restore/{id}")
    public ApiResponse<Void> restore(@PathVariable Long id) {

        service.restore(id);

        return ApiResponse.success(null, "Planning restored");
    }
}
