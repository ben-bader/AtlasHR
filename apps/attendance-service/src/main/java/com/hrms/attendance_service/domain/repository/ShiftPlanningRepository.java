package com.hrms.attendance_service.domain.repository;

import com.hrms.attendance_service.common.enums.PlanningStatus;
import com.hrms.attendance_service.domain.model.ShiftPlanning;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ShiftPlanningRepository extends JpaRepository<ShiftPlanning, Long> {

    // ================= EMPLOYEE =================

    List<ShiftPlanning> findByEmployeeIdAndDeletedFalse(
            String employeeId
    );

    Optional<ShiftPlanning>
    findTopByEmployeeIdAndEndDateIsNullAndDeletedFalse(
            String employeeId
    );

    // ================= STATUS =================

    List<ShiftPlanning> findByStatusAndDeletedFalse(
            PlanningStatus status
    );

    // ================= SHIFT =================

    List<ShiftPlanning> findByShiftIdAndDeletedFalse(
            Long shiftId
    );

    // ================= DATE RANGE =================

    List<ShiftPlanning>
    findByStartDateLessThanEqualAndEndDateGreaterThanEqualAndDeletedFalse(
            LocalDate startDate,
            LocalDate endDate
    );

    // ================= ACTIVE TODAY =================

    List<ShiftPlanning>
    findByStartDateLessThanEqualAndEndDateGreaterThanEqualAndStatusAndDeletedFalse(
            LocalDate today1,
            LocalDate today2,
            PlanningStatus status
    );

    // ================= DEPARTMENT =================

    List<ShiftPlanning>
    findByDepartmentNameAndDeletedFalse(
            String departmentName
    );

    // ================= EXISTS =================

    boolean existsByEmployeeIdAndStartDateAndDeletedFalse(
            String employeeId,
            LocalDate startDate
    );

    // ================= COUNT =================

    long countByStatusAndDeletedFalse(
            PlanningStatus status
    );

    Page<ShiftPlanning> findByEmployeeIdAndDeletedFalse(
        String employeeId,
        Pageable pageable
);
}
