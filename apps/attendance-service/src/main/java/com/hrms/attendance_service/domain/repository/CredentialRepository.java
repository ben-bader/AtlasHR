package com.hrms.attendance_service.domain.repository;

import com.hrms.attendance_service.domain.model.EmployeeVerificationCredential;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CredentialRepository extends JpaRepository<EmployeeVerificationCredential, Long> {

    Optional<EmployeeVerificationCredential> findByEmployeeId(String employeeId);
}
