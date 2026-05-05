package com.hrms.employee.presentation.controller;

import com.hrms.employee.application.dto.*;
import com.hrms.employee.application.service.EmployeeSkillService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/skills")
@Slf4j
public class SkillController {

    private final EmployeeSkillService skillService;

    public SkillController(EmployeeSkillService skillService) {
        this.skillService = skillService;
    }

    @PostMapping
    public ResponseEntity<EmployeeSkillResponse> addSkill(@RequestBody AddEmployeeSkillRequest request) {
        log.info("Adding skill for employee: {}", request.getEmployeeId());
        EmployeeSkillResponse response = skillService.addSkill(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<EmployeeSkillResponse>> getEmployeeSkills(@PathVariable String employeeId) {
        log.info("Fetching skills for employee: {}", employeeId);
        List<EmployeeSkillResponse> responses = skillService.getEmployeeSkills(employeeId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/search/{skillName}")
    public ResponseEntity<List<EmployeeSkillResponse>> getEmployeesBySkill(@PathVariable String skillName) {
        log.info("Finding employees with skill: {}", skillName);
        List<EmployeeSkillResponse> responses = skillService.getEmployeesBySkill(skillName);
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{skillId}")
    public ResponseEntity<EmployeeSkillResponse> updateSkill(@PathVariable String skillId,
                                                            @RequestBody AddEmployeeSkillRequest request) {
        log.info("Updating skill: {}", skillId);
        EmployeeSkillResponse response = skillService.updateSkill(skillId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{skillId}")
    public ResponseEntity<Void> removeSkill(@PathVariable String skillId) {
        log.info("Removing skill: {}", skillId);
        skillService.removeSkill(skillId);
        return ResponseEntity.noContent().build();
    }

}
