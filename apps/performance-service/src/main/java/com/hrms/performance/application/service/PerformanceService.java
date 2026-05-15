package com.hrms.performance.application.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
import com.hrms.performance.common.exceptions.ResourceNotFoundException;
import com.hrms.performance.domain.model.AppraisalCycle;
import com.hrms.performance.domain.model.AppraisalTemplate;
import com.hrms.performance.domain.model.FeedbackEntry;
import com.hrms.performance.domain.model.ManagerAppraisal;
import com.hrms.performance.domain.model.PerformanceGoal;
import com.hrms.performance.domain.model.SelfAppraisal;
import com.hrms.performance.domain.repository.AppraisalCycleRepository;
import com.hrms.performance.domain.repository.AppraisalTemplateRepository;
import com.hrms.performance.domain.repository.FeedbackRepository;
import com.hrms.performance.domain.repository.ManagerAppraisalRepository;
import com.hrms.performance.domain.repository.PerformanceGoalRepository;
import com.hrms.performance.domain.repository.SelfAppraisalRepository;

@Service
@Transactional
public class PerformanceService {

    private final AppraisalCycleRepository cycleRepository;
    private final AppraisalTemplateRepository templateRepository;
    private final PerformanceGoalRepository goalRepository;
    private final FeedbackRepository feedbackRepository;
    private final SelfAppraisalRepository selfRepository;
    private final ManagerAppraisalRepository managerRepository;

    public PerformanceService(AppraisalCycleRepository cycleRepository,
                              AppraisalTemplateRepository templateRepository,
                              PerformanceGoalRepository goalRepository,
                              FeedbackRepository feedbackRepository,
                              SelfAppraisalRepository selfRepository,
                              ManagerAppraisalRepository managerRepository) {
        this.cycleRepository = cycleRepository;
        this.templateRepository = templateRepository;
        this.goalRepository = goalRepository;
        this.feedbackRepository = feedbackRepository;
        this.selfRepository = selfRepository;
        this.managerRepository = managerRepository;
    }

    public AppraisalCycleResponse createCycle(AppraisalCycleRequest request) {
        AppraisalCycle cycle = new AppraisalCycle();
        cycle.setId(UUID.randomUUID().toString());
        cycle.setName(request.getName());
        cycle.setDescription(request.getDescription());
        cycle.setStartDate(request.getStartDate());
        cycle.setEndDate(request.getEndDate());
        cycle.setStatus(request.getStatus());
        cycle.setTemplateId(request.getTemplateId());
        AppraisalCycle saved = cycleRepository.save(cycle);
        return toCycleResponse(saved);
    }

    public AppraisalCycleResponse updateCycle(String cycleId, AppraisalCycleRequest request) {
        AppraisalCycle cycle = cycleRepository.findById(cycleId)
                .orElseThrow(() -> new ResourceNotFoundException("Appraisal cycle not found: " + cycleId));
        cycle.setName(request.getName());
        cycle.setDescription(request.getDescription());
        cycle.setStartDate(request.getStartDate());
        cycle.setEndDate(request.getEndDate());
        cycle.setStatus(request.getStatus());
        cycle.setTemplateId(request.getTemplateId());
        return toCycleResponse(cycleRepository.save(cycle));
    }

    public List<AppraisalCycleResponse> getCycles() {
        return cycleRepository.findAll().stream().map(this::toCycleResponse).collect(Collectors.toList());
    }

    public AppraisalTemplateResponse createTemplate(AppraisalTemplateRequest request) {
        AppraisalTemplate template = new AppraisalTemplate();
        template.setId(UUID.randomUUID().toString());
        template.setName(request.getName());
        template.setDescription(request.getDescription());
        template.setCriteria(request.getCriteria());
        return toTemplateResponse(templateRepository.save(template));
    }

    public List<AppraisalTemplateResponse> getTemplates() {
        return templateRepository.findAll().stream().map(this::toTemplateResponse).collect(Collectors.toList());
    }

    public PerformanceGoalResponse createGoal(PerformanceGoalRequest request) {
        PerformanceGoal goal = new PerformanceGoal();
        goal.setId(UUID.randomUUID().toString());
        goal.setEmployeeId(request.getEmployeeId());
        goal.setManagerId(request.getManagerId());
        goal.setTitle(request.getTitle());
        goal.setDescription(request.getDescription());
        goal.setTargetDate(request.getTargetDate());
        goal.setStatus(request.getStatus());
        goal.setProgress(request.getProgress());
        goal.setCycleId(request.getCycleId());
        return toGoalResponse(goalRepository.save(goal));
    }

    public PerformanceGoalResponse updateGoal(String goalId, PerformanceGoalRequest request) {
        PerformanceGoal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new ResourceNotFoundException("Performance goal not found: " + goalId));
        goal.setTitle(request.getTitle());
        goal.setDescription(request.getDescription());
        goal.setTargetDate(request.getTargetDate());
        goal.setStatus(request.getStatus());
        goal.setProgress(request.getProgress());
        goal.setManagerId(request.getManagerId());
        return toGoalResponse(goalRepository.save(goal));
    }

    public List<PerformanceGoalResponse> getGoals(String employeeId) {
        return goalRepository.findByEmployeeId(employeeId).stream().map(this::toGoalResponse).collect(Collectors.toList());
    }

    public SelfAppraisalResponse submitSelfAppraisal(SelfAppraisalRequest request) {
        SelfAppraisal appraisal = new SelfAppraisal();
        appraisal.setId(UUID.randomUUID().toString());
        appraisal.setEmployeeId(request.getEmployeeId());
        appraisal.setCycleId(request.getCycleId());
        appraisal.setStrengths(request.getStrengths());
        appraisal.setImprovements(request.getImprovements());
        appraisal.setGoalSummary(request.getGoalSummary());
        appraisal.setStatus(request.getStatus());
        appraisal.setSubmittedAt(LocalDateTime.now());
        return toSelfResponse(selfRepository.save(appraisal));
    }

    public ManagerAppraisalResponse submitManagerAppraisal(ManagerAppraisalRequest request) {
        ManagerAppraisal appraisal = new ManagerAppraisal();
        appraisal.setId(UUID.randomUUID().toString());
        appraisal.setEmployeeId(request.getEmployeeId());
        appraisal.setManagerId(request.getManagerId());
        appraisal.setCycleId(request.getCycleId());
        appraisal.setSummary(request.getSummary());
        appraisal.setRating(request.getRating());
        appraisal.setStatus(request.getStatus());
        appraisal.setSubmittedAt(LocalDateTime.now());
        return toManagerResponse(managerRepository.save(appraisal));
    }

    public FeedbackResponse addFeedback(FeedbackRequest request) {
        FeedbackEntry feedback = new FeedbackEntry();
        feedback.setId(UUID.randomUUID().toString());
        feedback.setEmployeeId(request.getEmployeeId());
        feedback.setManagerId(request.getManagerId());
        feedback.setCycleId(request.getCycleId());
        feedback.setComment(request.getComment());
        feedback.setRating(request.getRating());
        feedback.setType(request.getType());
        feedback.setCreatedAt(LocalDateTime.now());
        return toFeedbackResponse(feedbackRepository.save(feedback));
    }

    public List<FeedbackResponse> getFeedback(String employeeId) {
        return feedbackRepository.findByEmployeeId(employeeId).stream().map(this::toFeedbackResponse).collect(Collectors.toList());
    }

    public PerformanceReportResponse getReport(String employeeId) {
        List<PerformanceGoalResponse> goals = getGoals(employeeId);
        List<FeedbackResponse> feedback = getFeedback(employeeId);
        long completedGoals = goals.stream().filter(g -> g.getStatus() != null && g.getStatus().name().equals("COMPLETED")).count();

        PerformanceReportResponse report = new PerformanceReportResponse();
        report.setEmployeeId(employeeId);
        report.setTotalGoals(goals.size());
        report.setCompletedGoals((int) completedGoals);
        report.setSelfAppraisals(selfRepository.findByEmployeeId(employeeId).size());
        report.setManagerAppraisals(managerRepository.findByEmployeeId(employeeId).size());
        report.setFeedbackCount(feedback.size());
        report.setLastUpdated(LocalDateTime.now());
        return report;
    }

    private AppraisalCycleResponse toCycleResponse(AppraisalCycle cycle) {
        AppraisalCycleResponse response = new AppraisalCycleResponse();
        response.setId(cycle.getId());
        response.setName(cycle.getName());
        response.setDescription(cycle.getDescription());
        response.setStartDate(cycle.getStartDate());
        response.setEndDate(cycle.getEndDate());
        response.setStatus(cycle.getStatus());
        response.setTemplateId(cycle.getTemplateId());
        return response;
    }

    private AppraisalTemplateResponse toTemplateResponse(AppraisalTemplate template) {
        AppraisalTemplateResponse response = new AppraisalTemplateResponse();
        response.setId(template.getId());
        response.setName(template.getName());
        response.setDescription(template.getDescription());
        response.setCriteria(template.getCriteria());
        return response;
    }

    private PerformanceGoalResponse toGoalResponse(PerformanceGoal goal) {
        PerformanceGoalResponse response = new PerformanceGoalResponse();
        response.setId(goal.getId());
        response.setEmployeeId(goal.getEmployeeId());
        response.setManagerId(goal.getManagerId());
        response.setTitle(goal.getTitle());
        response.setDescription(goal.getDescription());
        response.setTargetDate(goal.getTargetDate());
        response.setStatus(goal.getStatus());
        response.setProgress(goal.getProgress());
        response.setCycleId(goal.getCycleId());
        return response;
    }

    private SelfAppraisalResponse toSelfResponse(SelfAppraisal appraisal) {
        SelfAppraisalResponse response = new SelfAppraisalResponse();
        response.setId(appraisal.getId());
        response.setEmployeeId(appraisal.getEmployeeId());
        response.setCycleId(appraisal.getCycleId());
        response.setStrengths(appraisal.getStrengths());
        response.setImprovements(appraisal.getImprovements());
        response.setGoalSummary(appraisal.getGoalSummary());
        response.setStatus(appraisal.getStatus());
        response.setSubmittedAt(appraisal.getSubmittedAt());
        return response;
    }

    private ManagerAppraisalResponse toManagerResponse(ManagerAppraisal appraisal) {
        ManagerAppraisalResponse response = new ManagerAppraisalResponse();
        response.setId(appraisal.getId());
        response.setEmployeeId(appraisal.getEmployeeId());
        response.setManagerId(appraisal.getManagerId());
        response.setCycleId(appraisal.getCycleId());
        response.setSummary(appraisal.getSummary());
        response.setRating(appraisal.getRating());
        response.setStatus(appraisal.getStatus());
        response.setSubmittedAt(appraisal.getSubmittedAt());
        return response;
    }

    private FeedbackResponse toFeedbackResponse(FeedbackEntry feedback) {
        FeedbackResponse response = new FeedbackResponse();
        response.setId(feedback.getId());
        response.setEmployeeId(feedback.getEmployeeId());
        response.setManagerId(feedback.getManagerId());
        response.setCycleId(feedback.getCycleId());
        response.setComment(feedback.getComment());
        response.setRating(feedback.getRating());
        response.setType(feedback.getType());
        response.setCreatedAt(feedback.getCreatedAt());
        return response;
    }
}
