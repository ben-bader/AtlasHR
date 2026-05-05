package com.hrms.employee.domain.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.hrms.employee.common.enums.DepartmentStatus;

@Entity
@Table(name = "departments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Department {

    @Id
    private Long departmentId;

    @Column(nullable = false, unique = true)
    private String departmentName;

    @Column(length = 500)
    private String description;

    @ManyToOne
    @JoinColumn(name = "parent_department_id")
    private Department parentDepartment;

    @OneToMany(mappedBy = "parentDepartment", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Department> subDepartments = new ArrayList<>();

    @Column(nullable = false)
    private String departmentCode;

    @ManyToOne
    @JoinColumn(name = "department_head_id")
    private Employee departmentHead;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private DepartmentStatus status;

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
