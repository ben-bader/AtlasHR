package com.hrms.employee.domain.repository;

import com.hrms.employee.domain.model.EmployeeSkill;
import com.hrms.employee.common.enums.SkillStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeSkillRepository extends JpaRepository<EmployeeSkill, String> {
    List<EmployeeSkill> findByEmployeeId(String employeeId);
    List<EmployeeSkill> findBySkillNameAndStatus(String skillName, SkillStatus status);
}
