package com.hrms.employee.presentation.controller;

import com.hrms.employee.application.dto.*;
import com.hrms.employee.application.service.DepartmentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/departments")
@Slf4j
public class DepartmentController {

    private final DepartmentService departmentService;

    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    @PostMapping
    public ResponseEntity<DepartmentResponse> createDepartment(@RequestBody CreateDepartmentRequest request) {
        log.info("Creating department: {}", request.getDepartmentName());
        DepartmentResponse response = departmentService.createDepartment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{departmentId}")
    public ResponseEntity<DepartmentResponse> getDepartmentById(@PathVariable Long departmentId) {
        log.info("Fetching department: {}", departmentId);
        DepartmentResponse response = departmentService.getDepartmentById(departmentId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<DepartmentResponse>> getAllDepartments() {
        log.info("Fetching all departments");
        List<DepartmentResponse> responses = departmentService.getAllDepartments();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{parentDepartmentId}/sub-departments")
    public ResponseEntity<List<DepartmentResponse>> getSubDepartments(@PathVariable Long parentDepartmentId) {
        log.info("Fetching sub-departments for: {}", parentDepartmentId);
        List<DepartmentResponse> responses = departmentService.getSubDepartments(parentDepartmentId);
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{departmentId}")
    public ResponseEntity<DepartmentResponse> updateDepartment(@PathVariable Long departmentId,
                                                              @RequestBody CreateDepartmentRequest request) {
        log.info("Updating department: {}", departmentId);
        DepartmentResponse response = departmentService.updateDepartment(departmentId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{departmentId}")
    public ResponseEntity<Void> deleteDepartment(@PathVariable Long departmentId) {
        log.info("Deleting department: {}", departmentId);
        departmentService.deleteDepartment(departmentId);
        return ResponseEntity.noContent().build();
    }

}
