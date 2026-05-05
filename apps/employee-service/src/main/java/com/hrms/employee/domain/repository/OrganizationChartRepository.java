package com.hrms.employee.domain.repository;

import com.hrms.employee.domain.model.OrganizationChart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrganizationChartRepository extends JpaRepository<OrganizationChart, String> {
    Optional<OrganizationChart> findByEmployeeId(String employeeId);
    List<OrganizationChart> findByManagerId(String managerId);
    
    @Query("SELECT oc FROM OrganizationChart oc WHERE oc.employee.department.departmentId = :departmentId")
    List<OrganizationChart> findByDepartmentDepartmentId(@Param("departmentId") Long departmentId);
}
