package com.hrms.employee.presentation.controller;

import com.hrms.employee.application.dto.*;
import com.hrms.employee.application.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/employees")
@Slf4j
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @PostMapping("/onboard")
    public ResponseEntity<EmployeeResponse> onboardEmployee(@RequestBody CreateEmployeeRequest request) {
        log.info("Received request to onboard employee: {}", request.getEmail());
        EmployeeResponse response = employeeService.onboardEmployee(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/promote")
    public ResponseEntity<EmployeeResponse> promoteEmployee(@RequestBody PromoteEmployeeRequest request) {
        log.info("Received request to promote employee: {}", request.getId());
        EmployeeResponse response = employeeService.promoteEmployee(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/transfer")
    public ResponseEntity<EmployeeResponse> transferEmployee(@RequestBody TransferEmployeeRequest request) {
        log.info("Received request to transfer employee: {}", request.getId());
        EmployeeResponse response = employeeService.transferEmployee(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/terminate")
    public ResponseEntity<EmployeeResponse> terminateEmployee(@RequestBody TerminateEmployeeRequest request) {
        log.info("Received request to terminate employee: {}", request.getId());
        EmployeeResponse response = employeeService.terminateEmployee(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{employeeId}")
    public ResponseEntity<EmployeeResponse> getEmployeeById(@PathVariable String employeeId) {
        log.info("Fetching employee: {}", employeeId);
        EmployeeResponse response = employeeService.getEmployeeById(employeeId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/department/{departmentId}")
    public ResponseEntity<List<EmployeeResponse>> getEmployeesByDepartment(@PathVariable Long departmentId) {
        log.info("Fetching employees for department: {}", departmentId);
        List<EmployeeResponse> responses = employeeService.getEmployeesByDepartment(departmentId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/active")
    public ResponseEntity<List<EmployeeResponse>> getAllActiveEmployees() {
        log.info("Fetching all active employees");
        List<EmployeeResponse> responses = employeeService.getAllActiveEmployees();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/manager/{managerId}/direct-reports")
    public ResponseEntity<List<EmployeeResponse>> getDirectReports(@PathVariable String managerId) {
        log.info("Fetching direct reports for manager: {}", managerId);
        List<EmployeeResponse> responses = employeeService.getDirectReports(managerId);
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{employeeId}")
    public ResponseEntity<EmployeeResponse> updateEmployee(@PathVariable String employeeId,
                                                          @RequestBody CreateEmployeeRequest request) {
        log.info("Updating employee: {}", employeeId);
        EmployeeResponse response = employeeService.updateEmployee(employeeId, request);
        return ResponseEntity.ok(response);
    }

}
