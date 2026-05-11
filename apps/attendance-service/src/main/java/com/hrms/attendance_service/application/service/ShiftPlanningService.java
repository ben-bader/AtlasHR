package com.hrms.attendance_service.application.service;

import com.hrms.attendance_service.common.enums.PlanningStatus;
import com.hrms.attendance_service.common.exceptions.BadRequestException;
import com.hrms.attendance_service.common.exceptions.ResourceNotFoundException;
import com.hrms.attendance_service.domain.model.ShiftPlanning;
import com.hrms.attendance_service.domain.repository.ShiftPlanningRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ShiftPlanningService {

    private final ShiftPlanningRepository planningRepository;

    // ================= CREATE =================
    public ShiftPlanning assignShift(ShiftPlanning planning) {

        boolean exists = planningRepository
                .existsByEmployeeIdAndStartDateAndDeletedFalse(
                        planning.getEmployeeId(),
                        planning.getStartDate()
                );

        if (exists) {
            throw new BadRequestException(
                    "Planning already exists for this employee and start date"
            );
        }

        return planningRepository.save(planning);
    }

    // ================= GET EMPLOYEE =================
    public List<ShiftPlanning> getEmployeePlanning(String employeeId) {

        return planningRepository
                .findByEmployeeIdAndDeletedFalse(employeeId);
    }

    // ================= GET ACTIVE =================
    public ShiftPlanning getActivePlanning(String employeeId) {

        return planningRepository
                .findTopByEmployeeIdAndEndDateIsNullAndDeletedFalse(employeeId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "No active planning found"
                        )
                );
    }

    // ================= GET BY STATUS =================
    public List<ShiftPlanning> getByStatus(PlanningStatus status) {

        return planningRepository
                .findByStatusAndDeletedFalse(status);
    }

    // ================= GET BY SHIFT =================
    public List<ShiftPlanning> getByShift(Long shiftId) {

        return planningRepository
                .findByShiftIdAndDeletedFalse(shiftId);
    }

    // ================= GET ACTIVE TODAY =================
    public List<ShiftPlanning> getActiveToday() {

        LocalDate today = LocalDate.now();

        return planningRepository
                .findByStartDateLessThanEqualAndEndDateGreaterThanEqualAndStatusAndDeletedFalse(
                        today,
                        today,
                        PlanningStatus.ACTIVE
                );
    }

    // ================= GET DEPARTMENT =================
    public List<ShiftPlanning> getDepartmentPlanning(
            String departmentName
    ) {

        return planningRepository
                .findByDepartmentNameAndDeletedFalse(departmentName);
    }

    // ================= DELETE =================
    public void deletePlanning(Long id) {

        ShiftPlanning planning = planningRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Planning not found"
                        )
                );

        planning.setDeleted(true);

        planningRepository.save(planning);
    }

    // ================= RESTORE =================
    public void restorePlanning(Long id) {

        ShiftPlanning planning = planningRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Planning not found"
                        )
                );

        planning.setDeleted(false);

        planningRepository.save(planning);
    }

    // ================= COUNT =================
    public long countActivePlanning() {

        return planningRepository
                .countByStatusAndDeletedFalse(
                        PlanningStatus.ACTIVE
                );
    }

    public Page<ShiftPlanning> getEmployeePlanning(
            String employeeId,
            Pageable pageable
    ) {

        return planningRepository
                .findByEmployeeIdAndDeletedFalse(
                        employeeId,
                        pageable
                );
    }
}
