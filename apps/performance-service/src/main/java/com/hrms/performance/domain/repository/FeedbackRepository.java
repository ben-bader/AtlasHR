package com.hrms.performance.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hrms.performance.domain.model.FeedbackEntry;

@Repository
public interface FeedbackRepository extends JpaRepository<FeedbackEntry, String> {
    List<FeedbackEntry> findByEmployeeId(String employeeId);
}
