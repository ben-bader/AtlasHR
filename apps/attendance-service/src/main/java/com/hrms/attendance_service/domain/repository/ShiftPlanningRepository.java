package com.hrms.attendance_service.domain.repository;

import com.hrms.attendance_service.domain.model.ShiftPlanning;
import com.hrms.attendance_service.common.enums.PlanningStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ShiftPlanningRepository extends JpaRepository<ShiftPlanning, Long> {

    List<ShiftPlanning> findByEmployeeId(String employeeId);

    List<ShiftPlanning> findByStatus(PlanningStatus status);

    List<ShiftPlanning> findByStartDateLessThanEqualAndEndDateGreaterThanEqual(
            LocalDate startDate,
            LocalDate endDate
    );

    Optional<ShiftPlanning> findTopByEmployeeIdAndEndDateIsNull(
        String employeeId
    );
}
