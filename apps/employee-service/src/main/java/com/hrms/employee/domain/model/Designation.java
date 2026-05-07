package com.hrms.employee.domain.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

import com.hrms.employee.common.enums.DesignationStatus;

@Entity
@Table(name = "designations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Designation {

    @Id
    @Column(name = "designation_id", length = 20)
    private String id;

    @Column(nullable = false, unique = true)
    private String designationName;

    @Column(length = 500)
    private String description;

    @Column(nullable = false)
    private String designationCode;

    @Column(nullable = false)
    private Integer hierarchyLevel;

    private String reportingDesignation;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private DesignationStatus status;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

}
