package com.hrms.attendance_service.presentation.controller;

import com.hrms.attendance_service.application.dto.*;
import com.hrms.attendance_service.application.mapper.ShiftPlanningMapper;
import com.hrms.attendance_service.application.service.ShiftPlanningService;
import com.hrms.attendance_service.common.api.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/shift-plannings")
@RequiredArgsConstructor
public class ShiftPlanningController {

    private final ShiftPlanningService service;
    private final ShiftPlanningMapper mapper;

    // ================= ASSIGN =================
    @PostMapping
    public ApiResponse<ShiftPlanningResponseDTO> assign(
            @Valid @RequestBody ShiftPlanningRequestDTO request
    ) {

        var saved = service.assignShift(
                mapper.toEntity(request)
        );

        return ApiResponse.success(
                mapper.toDto(saved),
                "Shift assigned"
        );
    }

    // ================= EMPLOYEE PLANNING =================
    @GetMapping("/employee/{employeeId}")
    public ApiResponse<List<ShiftPlanningResponseDTO>> getEmployee(
            @PathVariable String employeeId
    ) {

        var result = service.getEmployeePlanning(employeeId)
                .stream()
                .map(mapper::toDto)
                .toList();

        return ApiResponse.success(result, "Employee planning");
    }

    // ================= ACTIVE =================
    @GetMapping("/employee/{employeeId}/active")
    public ApiResponse<ShiftPlanningResponseDTO> getActive(
            @PathVariable String employeeId
    ) {

        return ApiResponse.success(
                mapper.toDto(service.getActivePlanning(employeeId)),
                "Active planning"
        );
    }
}
