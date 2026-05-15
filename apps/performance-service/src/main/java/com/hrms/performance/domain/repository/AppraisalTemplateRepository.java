package com.hrms.performance.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hrms.performance.domain.model.AppraisalTemplate;

@Repository
public interface AppraisalTemplateRepository extends JpaRepository<AppraisalTemplate, String> {
}
