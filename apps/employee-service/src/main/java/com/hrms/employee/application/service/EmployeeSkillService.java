package com.hrms.employee.application.service;

import com.hrms.employee.application.dto.*;
import com.hrms.employee.common.enums.CompetencyLevel;
import com.hrms.employee.common.enums.SkillStatus;
import com.hrms.employee.common.utils.IdGeneratorFactory;
import com.hrms.employee.domain.model.*;
import com.hrms.employee.domain.repository.EmployeeRepository;
import com.hrms.employee.domain.repository.EmployeeSkillRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@Transactional
public class EmployeeSkillService {

    private final EmployeeSkillRepository employeeSkillRepository;
    private final EmployeeRepository employeeRepository;
    private final IdGeneratorFactory idGeneratorFactory;

    public EmployeeSkillService(EmployeeSkillRepository employeeSkillRepository,
                               EmployeeRepository employeeRepository,
                               IdGeneratorFactory idGeneratorFactory) {
        this.employeeSkillRepository = employeeSkillRepository;
        this.employeeRepository = employeeRepository;
        this.idGeneratorFactory = idGeneratorFactory;
    }

    public EmployeeSkillResponse addSkill(AddEmployeeSkillRequest request) {
        log.info("Adding skill to employee: {}", request.getEmployeeId());

        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        EmployeeSkill skill = EmployeeSkill.builder()
                .skillId(idGeneratorFactory.generateSkillId())
                .employee(employee)
                .skillName(request.getSkillName())
                .competencyLevel(CompetencyLevel.valueOf(request.getCompetencyLevel()))
                .certification(request.getCertification())
                .status(SkillStatus.ACTIVE)
                .build();

        skill = employeeSkillRepository.save(skill);
        log.info("Skill added to employee: {}", skill.getSkillId());

        return mapToResponse(skill);
    }

    public EmployeeSkillResponse updateSkill(String skillId, AddEmployeeSkillRequest request) {
        log.info("Updating skill: {}", skillId);

        EmployeeSkill skill = employeeSkillRepository.findById(skillId)
                .orElseThrow(() -> new RuntimeException("Skill not found"));

        skill.setSkillName(request.getSkillName());
        skill.setCompetencyLevel(CompetencyLevel.valueOf(request.getCompetencyLevel()));
        skill.setCertification(request.getCertification());

        skill = employeeSkillRepository.save(skill);
        return mapToResponse(skill);
    }

    public List<EmployeeSkillResponse> getEmployeeSkills(String employeeId) {
        log.info("Retrieving skills for employee: {}", employeeId);
        List<EmployeeSkill> skills = employeeSkillRepository.findByEmployeeId(employeeId);
        return skills.stream().map(this::mapToResponse).toList();
    }

    public void removeSkill(String skillId) {
        log.info("Removing skill: {}", skillId);
        EmployeeSkill skill = employeeSkillRepository.findById(skillId)
                .orElseThrow(() -> new RuntimeException("Skill not found"));
        skill.setStatus(SkillStatus.INACTIVE);
        employeeSkillRepository.save(skill);
    }

    public List<EmployeeSkillResponse> getEmployeesBySkill(String skillName) {
        log.info("Finding employees with skill: {}", skillName);
        List<EmployeeSkill> skills = employeeSkillRepository
                .findBySkillNameAndStatus(skillName, SkillStatus.ACTIVE);
        return skills.stream().map(this::mapToResponse).toList();
    }

    private EmployeeSkillResponse mapToResponse(EmployeeSkill skill) {
        return EmployeeSkillResponse.builder()
                .skillId(skill.getSkillId())
                .employeeId(skill.getEmployee().getEmployeeId())
                .skillName(skill.getSkillName())
                .competencyLevel(skill.getCompetencyLevel().toString())
                .certification(skill.getCertification())
                .status(skill.getStatus().toString())
                .build();
    }

}
