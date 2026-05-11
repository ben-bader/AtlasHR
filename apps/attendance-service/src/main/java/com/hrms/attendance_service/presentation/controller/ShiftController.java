package com.hrms.attendance_service.presentation.controller;

import com.hrms.attendance_service.application.dto.ShiftRequestDTO;
import com.hrms.attendance_service.application.dto.ShiftResponseDTO;
import com.hrms.attendance_service.application.mapper.ShiftMapper;
import com.hrms.attendance_service.application.service.ShiftService;
import com.hrms.attendance_service.common.api.ApiResponse;
import com.hrms.attendance_service.domain.model.Shift;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shifts")
@RequiredArgsConstructor
public class ShiftController {

    private final ShiftService shiftService;
    private final ShiftMapper shiftMapper;

    // ================= CREATE SHIFT =================
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ApiResponse<ShiftResponseDTO> createShift(
            @Valid @RequestBody ShiftRequestDTO request
    ) {

        Shift shift = shiftMapper.toEntity(request);
        Shift saved = shiftService.createShift(shift);

        return ApiResponse.success(
                shiftMapper.toDto(saved),
                "Shift created successfully"
        );
    }

    // ================= GET ALL ACTIVE =================
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @GetMapping
    public ApiResponse<List<ShiftResponseDTO>> getAllActive() {

        List<ShiftResponseDTO> result = shiftService.getAllActiveShifts()
                .stream()
                .map(shiftMapper::toDto)
                .toList();

        return ApiResponse.success(result, "Active shifts");
    }

    // ================= GET BY ID =================
        @PreAuthorize("hasAnyRole('ADMIN','HR')")
        @GetMapping("/{id}")
        public ApiResponse<ShiftResponseDTO> getById(@PathVariable Long id) {

                Shift shift = shiftService.getById(id);

                return ApiResponse.success(
                        shiftMapper.toDto(shift),
                        "Shift details"
                );
        }

    // ================= DELETE SHIFT =================
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteShift(@PathVariable Long id) {

        shiftService.deleteShift(id);

        return ApiResponse.success(null, "Shift deleted (soft delete)");
    }
}
