package com.hrms.performance.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hrms.performance.domain.model.SelfAppraisal;

@Repository
public interface SelfAppraisalRepository extends JpaRepository<SelfAppraisal, String> {
    List<SelfAppraisal> findByEmployeeId(String employeeId);
}
