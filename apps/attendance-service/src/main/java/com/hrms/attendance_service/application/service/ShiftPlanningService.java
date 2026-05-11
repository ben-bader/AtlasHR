package com.hrms.attendance_service.application.service;

import com.hrms.attendance_service.domain.model.ShiftPlanning;
import com.hrms.attendance_service.domain.repository.ShiftPlanningRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ShiftPlanningService {

    private final ShiftPlanningRepository planningRepository;

    public ShiftPlanning assignShift(ShiftPlanning planning) {
        return planningRepository.save(planning);
    }

    public List<ShiftPlanning> getEmployeePlanning(String employeeId) {
        return planningRepository.findByEmployeeId(employeeId);
    }

    public ShiftPlanning getActivePlanning(String employeeId) {
        return planningRepository
                .findTopByEmployeeIdAndEndDateIsNull(employeeId)
                .orElseThrow(() -> new RuntimeException("No active planning"));
    }
}
