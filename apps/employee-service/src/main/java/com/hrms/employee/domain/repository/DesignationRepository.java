package com.hrms.employee.domain.repository;

import com.hrms.employee.domain.model.Designation;
import com.hrms.employee.common.enums.DesignationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DesignationRepository extends JpaRepository<Designation, String> {
    Optional<Designation> findByDesignationName(String designationName);
    Optional<Designation> findByDesignationCode(String designationCode);
    List<Designation> findByStatus(DesignationStatus status);
}
