package com.hrms.employee.domain.repository;

import com.hrms.employee.domain.model.Department;
import com.hrms.employee.common.enums.DepartmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {
    Optional<Department> findByDepartmentName(String departmentName);
    Optional<Department> findByDepartmentCode(String departmentCode);
    List<Department> findByStatus(DepartmentStatus status);
    List<Department> findByParentDepartmentDepartmentId(Long parentDepartmentId);
}
