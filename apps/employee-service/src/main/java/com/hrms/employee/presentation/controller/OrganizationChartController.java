package com.hrms.employee.presentation.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hrms.employee.application.service.OrganizationChartService;
import com.hrms.employee.domain.model.OrganizationChart;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/organization-chart")
@Slf4j
public class OrganizationChartController {

    private final OrganizationChartService organizationChartService;

    public OrganizationChartController(OrganizationChartService organizationChartService) {
        this.organizationChartService = organizationChartService;
    }

    @GetMapping("/manager/{managerId}")
    public ResponseEntity<List<OrganizationChart>> getEmployeesByManager(@PathVariable String managerId) {
        log.info("Fetching org chart for manager: {}", managerId);
        List<OrganizationChart> charts = organizationChartService.getEmployeesByManager(managerId);
        return ResponseEntity.ok(charts);
    }

    @GetMapping("/department/{departmentId}")
    public ResponseEntity<List<OrganizationChart>> getDepartmentHierarchy(@PathVariable Long departmentId) {
        log.info("Fetching org chart for department: {}", departmentId);
        List<OrganizationChart> charts = organizationChartService.getDepartmentHierarchy(departmentId);
        return ResponseEntity.ok(charts);
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<OrganizationChart> getEmployeeHierarchy(@PathVariable String employeeId) {
        log.info("Fetching hierarchy for employee: {}", employeeId);
        OrganizationChart chart = organizationChartService.getEmployeeHierarchy(employeeId);
        return ResponseEntity.ok(chart);
    }

}
