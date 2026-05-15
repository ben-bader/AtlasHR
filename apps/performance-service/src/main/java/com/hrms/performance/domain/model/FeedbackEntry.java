package com.hrms.performance.domain.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import com.hrms.performance.common.enums.FeedbackType;
import lombok.Data;

@Entity
@Table(name = "performance_feedback")
@Data
public class FeedbackEntry {

    @Id
    private String id;
    private String employeeId;
    private String managerId;
    private String cycleId;
    private String comment;
    private Integer rating;

    @Enumerated(EnumType.STRING)
    private FeedbackType type;
    private LocalDateTime createdAt;
}
