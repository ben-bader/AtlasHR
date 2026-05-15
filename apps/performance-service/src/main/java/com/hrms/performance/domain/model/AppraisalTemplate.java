package com.hrms.performance.domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "appraisal_templates")
@Data
public class AppraisalTemplate {

    @Id
    private String id;
    private String name;
    private String description;
    private String criteria;
}
