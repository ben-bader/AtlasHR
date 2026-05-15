package com.hrms.performance.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hrms.performance.domain.model.ManagerAppraisal;

@Repository
public interface ManagerAppraisalRepository extends JpaRepository<ManagerAppraisal, String> {
    List<ManagerAppraisal> findByEmployeeId(String employeeId);
}
