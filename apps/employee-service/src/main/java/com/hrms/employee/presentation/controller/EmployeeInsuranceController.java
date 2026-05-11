package com.hrms.employee.presentation.controller;

import com.hrms.employee.application.dto.AddInsuranceRequest;
import com.hrms.employee.application.dto.EmployeeInsuranceResponse;
import com.hrms.employee.application.service.EmployeeInsuranceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/employees/insurances")
@Slf4j
public class EmployeeInsuranceController {

    private final EmployeeInsuranceService employeeInsuranceService;

    public EmployeeInsuranceController(EmployeeInsuranceService employeeInsuranceService) {
        this.employeeInsuranceService = employeeInsuranceService;
    }

    @PostMapping
    public ResponseEntity<EmployeeInsuranceResponse> addInsurance(@RequestBody AddInsuranceRequest request) {
        log.info("Adding insurance for employee: {}", request.getId());
        EmployeeInsuranceResponse response = employeeInsuranceService.addInsurance(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<EmployeeInsuranceResponse>> getEmployeeInsurances(@PathVariable String employeeId) {
        log.info("Fetching insurances for employee: {}", employeeId);
        List<EmployeeInsuranceResponse> responses = employeeInsuranceService.getEmployeeInsurances(employeeId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/employee/{employeeId}/active")
    public ResponseEntity<List<EmployeeInsuranceResponse>> getActiveInsurances(@PathVariable String employeeId) {
        log.info("Fetching active insurances for employee: {}", employeeId);
        List<EmployeeInsuranceResponse> responses = employeeInsuranceService.getActiveInsurances(employeeId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/employee/{employeeId}/type/{insuranceType}")
    public ResponseEntity<List<EmployeeInsuranceResponse>> getInsurancesByType(
            @PathVariable String employeeId,
            @PathVariable String insuranceType) {
        log.info("Fetching {} insurances for employee: {}", insuranceType, employeeId);
        List<EmployeeInsuranceResponse> responses = employeeInsuranceService.getInsurancesByType(employeeId, insuranceType);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/policy/{policyNumber}")
    public ResponseEntity<EmployeeInsuranceResponse> getInsuranceByPolicyNumber(@PathVariable String policyNumber) {
        log.info("Fetching insurance by policy number: {}", policyNumber);
        EmployeeInsuranceResponse response = employeeInsuranceService.getInsuranceByPolicyNumber(policyNumber);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{insuranceId}/status")
    public ResponseEntity<EmployeeInsuranceResponse> updateInsuranceStatus(
            @PathVariable String insuranceId,
            @RequestParam String status) {
        log.info("Updating insurance status: {}", insuranceId);
        EmployeeInsuranceResponse response = employeeInsuranceService.updateInsuranceStatus(insuranceId, status);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{insuranceId}/claim")
    public ResponseEntity<EmployeeInsuranceResponse> fileInsuranceClaim(
            @PathVariable String insuranceId,
            @RequestParam Double claimAmount,
            @RequestParam String claimDetails) {
        log.info("Filing claim for insurance: {}", insuranceId);
        EmployeeInsuranceResponse response = employeeInsuranceService.fileInsuranceClaim(insuranceId, claimAmount, claimDetails);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{insuranceId}")
    public ResponseEntity<Void> deleteInsurance(@PathVariable String insuranceId) {
        log.info("Deleting insurance: {}", insuranceId);
        employeeInsuranceService.deleteInsurance(insuranceId);
        return ResponseEntity.noContent().build();
    }

}
