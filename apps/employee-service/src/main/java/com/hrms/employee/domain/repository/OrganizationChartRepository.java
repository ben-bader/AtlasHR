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

    // correct — chart.employee.employeeId
    Optional<OrganizationChart> findByEmployee_EmployeeId(String employeeId);

    // ❌ removed findByManagerId
    // ✅ correct nested path
    List<OrganizationChart> findByManager_EmployeeId(String employeeId);

    // keep JPQL but fix path clarity
    @Query("""
        SELECT oc 
        FROM OrganizationChart oc 
        WHERE oc.employee.department.departmentId = :departmentId
    """)
    List<OrganizationChart> findByDepartment_DepartmentId(@Param("departmentId") Long departmentId);
}