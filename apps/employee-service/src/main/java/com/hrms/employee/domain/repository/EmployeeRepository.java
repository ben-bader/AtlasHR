package com.hrms.employee.domain.repository;

import com.hrms.employee.domain.model.Employee;
import com.hrms.employee.common.enums.EmploymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, String> {
    Optional<Employee> findByEmail(String email);
    Optional<Employee> findByPhone(String phone);
    List<Employee> findByDepartmentDepartmentId(Long departmentId);
    List<Employee> findByStatus(EmploymentStatus status);
    List<Employee> findByReportingManagerEmployeeId(String managerId);
}
