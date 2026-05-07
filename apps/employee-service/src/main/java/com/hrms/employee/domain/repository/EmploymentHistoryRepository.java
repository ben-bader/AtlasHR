package com.hrms.employee.domain.repository;

import com.hrms.employee.domain.model.EmploymentHistory;
import com.hrms.employee.common.enums.EmploymentChangeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmploymentHistoryRepository extends JpaRepository<EmploymentHistory, String> {

    @Query("SELECT h FROM EmploymentHistory h WHERE h.employee.employeeId = :employeeId ORDER BY h.effectiveDate DESC")
    List<EmploymentHistory> findByEmployeeIdOrderByEffectiveDateDesc(@Param("employeeId") String employeeId);
    List<EmploymentHistory> findByChangeType(EmploymentChangeType changeType);
}
