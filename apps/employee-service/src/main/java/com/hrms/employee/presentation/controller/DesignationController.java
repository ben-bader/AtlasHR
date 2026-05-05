package com.hrms.employee.presentation.controller;

import com.hrms.employee.application.dto.*;
import com.hrms.employee.application.service.DesignationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/designations")
@Slf4j
public class DesignationController {

    private final DesignationService designationService;

    public DesignationController(DesignationService designationService) {
        this.designationService = designationService;
    }

    @PostMapping
    public ResponseEntity<DesignationResponse> createDesignation(@RequestBody CreateDesignationRequest request) {
        log.info("Creating designation: {}", request.getDesignationName());
        DesignationResponse response = designationService.createDesignation(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{designationId}")
    public ResponseEntity<DesignationResponse> getDesignationById(@PathVariable String designationId) {
        log.info("Fetching designation: {}", designationId);
        DesignationResponse response = designationService.getDesignationById(designationId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<DesignationResponse>> getAllDesignations() {
        log.info("Fetching all designations");
        List<DesignationResponse> responses = designationService.getAllDesignations();
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{designationId}")
    public ResponseEntity<DesignationResponse> updateDesignation(@PathVariable String designationId,
                                                                @RequestBody CreateDesignationRequest request) {
        log.info("Updating designation: {}", designationId);
        DesignationResponse response = designationService.updateDesignation(designationId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{designationId}")
    public ResponseEntity<Void> deleteDesignation(@PathVariable String designationId) {
        log.info("Deleting designation: {}", designationId);
        designationService.deleteDesignation(designationId);
        return ResponseEntity.noContent().build();
    }

}
