package com.hrms.employee.domain.repository;

import com.hrms.employee.domain.model.EmploymentHistory;
import com.hrms.employee.common.enums.EmploymentChangeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmploymentHistoryRepository extends JpaRepository<EmploymentHistory, String> {
    List<EmploymentHistory> findByEmployeeIdOrderByEffectiveDateDesc(String employeeId);
    List<EmploymentHistory> findByChangeType(EmploymentChangeType changeType);
}
