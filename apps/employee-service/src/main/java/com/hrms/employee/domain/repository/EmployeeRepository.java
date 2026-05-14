package com.hrms.employee.domain.repository;

import com.hrms.employee.domain.model.Employee;
import com.hrms.employee.common.enums.EmploymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, String> {

    Optional<Employee> findByEmail(String email);

    Optional<Employee> findByContactInfo_PrimaryPhone(String primaryPhone);

    @Query("SELECT e FROM Employee e WHERE e.department.id = :departmentId")
    List<Employee> findEmployeesByDepartmentId(@Param("departmentId") Long departmentId);

    List<Employee> findByStatus(EmploymentStatus status);

    // 🔥 FIXED (nested property path)
    List<Employee> findByReportingManager_Id(String employeeId);
}