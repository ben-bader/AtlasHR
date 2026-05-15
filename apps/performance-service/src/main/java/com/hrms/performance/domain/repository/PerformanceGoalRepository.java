package com.hrms.performance.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hrms.performance.domain.model.PerformanceGoal;

@Repository
public interface PerformanceGoalRepository extends JpaRepository<PerformanceGoal, String> {
    List<PerformanceGoal> findByEmployeeId(String employeeId);
}
