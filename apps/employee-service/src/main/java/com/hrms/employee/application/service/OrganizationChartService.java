package com.hrms.employee.application.service;

import com.hrms.employee.domain.model.OrganizationChart;
import com.hrms.employee.domain.repository.OrganizationChartRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@Transactional
public class OrganizationChartService {

    private final OrganizationChartRepository repository;

    public OrganizationChartService(OrganizationChartRepository repository) {
        this.repository = repository;
    }

    public List<OrganizationChart> getEmployeesByManager(String managerId) {
        log.info("Retrieving org chart for manager: {}", managerId);
        return repository.findByManager_Id(managerId);
    }

    public List<OrganizationChart> getDepartmentHierarchy(Long departmentId) {
        log.info("Retrieving org chart for department: {}", departmentId);
        return repository.findByDepartment_DepartmentId(departmentId);
    }

    public OrganizationChart getEmployeeHierarchy(String employeeId) {
        log.info("Retrieving hierarchy for employee: {}", employeeId);
        return repository.findByEmployee_Id(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee hierarchy not found"));
    }
}