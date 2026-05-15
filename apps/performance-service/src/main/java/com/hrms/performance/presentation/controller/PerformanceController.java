package com.hrms.performance.presentation.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hrms.performance.application.dto.AppraisalCycleRequest;
import com.hrms.performance.application.dto.AppraisalCycleResponse;
import com.hrms.performance.application.dto.AppraisalTemplateRequest;
import com.hrms.performance.application.dto.AppraisalTemplateResponse;
import com.hrms.performance.application.dto.FeedbackRequest;
import com.hrms.performance.application.dto.FeedbackResponse;
import com.hrms.performance.application.dto.ManagerAppraisalRequest;
import com.hrms.performance.application.dto.ManagerAppraisalResponse;
import com.hrms.performance.application.dto.PerformanceGoalRequest;
import com.hrms.performance.application.dto.PerformanceGoalResponse;
import com.hrms.performance.application.dto.PerformanceReportResponse;
import com.hrms.performance.application.dto.SelfAppraisalRequest;
import com.hrms.performance.application.dto.SelfAppraisalResponse;
import com.hrms.performance.application.service.PerformanceService;

@RestController
@RequestMapping("/api/performance")
public class PerformanceController {

    private final PerformanceService performanceService;

    public PerformanceController(PerformanceService performanceService) {
        this.performanceService = performanceService;
    }

    @PostMapping("/cycles")
    public ResponseEntity<AppraisalCycleResponse> createCycle(@Valid @RequestBody AppraisalCycleRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(performanceService.createCycle(request));
    }

    @PutMapping("/cycles/{cycleId}")
    public ResponseEntity<AppraisalCycleResponse> updateCycle(@PathVariable String cycleId,
                                                              @Valid @RequestBody AppraisalCycleRequest request) {
        return ResponseEntity.ok(performanceService.updateCycle(cycleId, request));
    }

    @GetMapping("/cycles")
    public ResponseEntity<List<AppraisalCycleResponse>> getCycles() {
        return ResponseEntity.ok(performanceService.getCycles());
    }

    @PostMapping("/templates")
    public ResponseEntity<AppraisalTemplateResponse> createTemplate(@Valid @RequestBody AppraisalTemplateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(performanceService.createTemplate(request));
    }

    @GetMapping("/templates")
    public ResponseEntity<List<AppraisalTemplateResponse>> getTemplates() {
        return ResponseEntity.ok(performanceService.getTemplates());
    }

    @PostMapping("/goals")
    public ResponseEntity<PerformanceGoalResponse> createGoal(@Valid @RequestBody PerformanceGoalRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(performanceService.createGoal(request));
    }

    @PutMapping("/goals/{goalId}")
    public ResponseEntity<PerformanceGoalResponse> updateGoal(@PathVariable String goalId,
                                                              @Valid @RequestBody PerformanceGoalRequest request) {
        return ResponseEntity.ok(performanceService.updateGoal(goalId, request));
    }

    @GetMapping("/goals/employee/{employeeId}")
    public ResponseEntity<List<PerformanceGoalResponse>> getGoals(@PathVariable String employeeId) {
        return ResponseEntity.ok(performanceService.getGoals(employeeId));
    }

    @PostMapping("/self-appraisals")
    public ResponseEntity<SelfAppraisalResponse> submitSelfAppraisal(@Valid @RequestBody SelfAppraisalRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(performanceService.submitSelfAppraisal(request));
    }

    @PostMapping("/manager-appraisals")
    public ResponseEntity<ManagerAppraisalResponse> submitManagerAppraisal(@Valid @RequestBody ManagerAppraisalRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(performanceService.submitManagerAppraisal(request));
    }

    @PostMapping("/feedback")
    public ResponseEntity<FeedbackResponse> addFeedback(@Valid @RequestBody FeedbackRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(performanceService.addFeedback(request));
    }

    @GetMapping("/feedback/employee/{employeeId}")
    public ResponseEntity<List<FeedbackResponse>> getFeedback(@PathVariable String employeeId) {
        return ResponseEntity.ok(performanceService.getFeedback(employeeId));
    }

    @GetMapping("/reports/employee/{employeeId}")
    public ResponseEntity<PerformanceReportResponse> getReport(@PathVariable String employeeId) {
        return ResponseEntity.ok(performanceService.getReport(employeeId));
    }
}
