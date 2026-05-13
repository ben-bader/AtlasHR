package com.hrms.attendance_service.application.service;

import com.hrms.attendance_service.common.enums.PlanningStatus;
import com.hrms.attendance_service.common.exceptions.BadRequestException;
import com.hrms.attendance_service.common.exceptions.ResourceNotFoundException;
import com.hrms.attendance_service.domain.model.ShiftPlanning;
import com.hrms.attendance_service.domain.repository.ShiftPlanningRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ShiftPlanningService {

    private final ShiftPlanningRepository repository;

    // =====================================================
    // CREATE / ASSIGN SHIFT (ROSTER ASSIGNMENT)
    // =====================================================

    public ShiftPlanning assignShift(ShiftPlanning planning) {

        // conflict check
        boolean exists = repository
                .existsByEmployeeIdAndStartDateLessThanEqualAndEndDateGreaterThanEqualAndDeletedFalse(
                        planning.getEmployeeId(),
                        planning.getEndDate(),
                        planning.getStartDate()
                );

        if (exists) {
            throw new BadRequestException(
                    "Employee already has a shift in this period"
            );
        }

        return repository.save(planning);
    }

    // =====================================================
    // EMPLOYEE PLANNING (FULL HISTORY)
    // =====================================================

    public List<ShiftPlanning> getEmployeePlanning(String employeeId) {

        return repository.findByEmployeeIdAndDeletedFalse(employeeId);
    }

    // pagination
    public Page<ShiftPlanning> getEmployeePlanningPaginated(
            String employeeId,
            int page,
            int size
    ) {

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("startDate").descending()
        );

        return repository.findByEmployeeIdAndDeletedFalse(
                employeeId,
                pageable
        );
    }

    // =====================================================
    // ACTIVE PLANNING (CURRENT SHIFT)
    // =====================================================

    public ShiftPlanning getActivePlanning(String employeeId) {

        return repository
                .findTopByEmployeeIdAndEndDateIsNullAndDeletedFalse(employeeId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "No active planning found"
                        )
                );
    }

    // =====================================================
    // LATEST PLANNING (ROSTER UI)
    // =====================================================

    public ShiftPlanning getLatestPlanning(String employeeId) {

        return repository
                .findTopByEmployeeIdAndDeletedFalseOrderByStartDateDesc(employeeId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "No planning found"
                        )
                );
    }

    // =====================================================
    // TEAM ROSTER
    // =====================================================

    public List<ShiftPlanning> getTeamPlanning(String teamName) {

        return repository.findByTeamNameAndDeletedFalse(teamName);
    }

    // =====================================================
    // DEPARTMENT ROSTER
    // =====================================================

    public List<ShiftPlanning> getDepartmentPlanning(String department) {

        return repository.findByDepartmentNameAndDeletedFalse(department);
    }

    // =====================================================
    // STATUS FILTER
    // =====================================================

    public List<ShiftPlanning> getByStatus(PlanningStatus status) {

        return repository.findByStatusAndDeletedFalse(status);
    }

    public long countByStatus(PlanningStatus status) {

        return repository.countByStatusAndDeletedFalse(status);
    }

    // DATE RANGE (ROSTER CALENDAR VIEW)
    // =====================================================

    public List<ShiftPlanning> getByDateRange(LocalDate start, LocalDate end) {

        return repository.findByStartDateLessThanEqualAndEndDateGreaterThanEqualAndDeletedFalse(start,end);

    }

    // TODAY ROSTER
    // =====================================================

    public List<ShiftPlanning> getTodayRoster() {

        LocalDate today = LocalDate.now();

        return repository.findByStartDateLessThanEqualAndEndDateGreaterThanEqualAndDeletedFalse(
                today,
                today
        );
    }

    // FLEXIBLE SHIFTS
    // =====================================================

    public List<ShiftPlanning> getFlexibleShifts() {

        return repository.findByFlexibleShiftTrueAndDeletedFalse();
    }

    // AUTO GENERATED ROSTER
    // =====================================================

    public List<ShiftPlanning> getAutoGenerated() {

        return repository.findByAutoGeneratedTrueAndDeletedFalse();
    }

    // DELETE
    // =====================================================

    public void delete(Long id) {

        ShiftPlanning planning = repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Planning not found")
                );

        planning.setDeleted(true);

        repository.save(planning);
    }

    // RESTORE
    // =====================================================

    public void restore(Long id) {

        ShiftPlanning planning = repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Planning not found")
                );

        planning.setDeleted(false);

        repository.save(planning);
    }
}
